package piaf.petrinet;

import piaf.threads.CyclicThread;
import piaf.tools.FixedSizeQueue;

/**
 * This is a implementation of a PetriNet engine.
 * The information about the net is kept in the place and transition classes
 * 
 * @author mb
 *
 */
public class PetriNetEngine extends CyclicThread {
	
	final static byte MAX_ACTIVE_PLACES = 8;
	
	private NetStateMonitor StateMon;
	
	private Place StartingPlace;
	private Place StoppingPlace;
	private boolean Stopping_Place_was_deactivated;
	
	private FixedSizeQueue<Place>      Active_Places;
	private FixedSizeQueue<Transition> Possible_Transitions;
	
	private PetriNet __Net;

	/**
	 * Constructor
	 *
	 * @param priority
	 * @param cycle_Time Cycle Time of the PetriNet Thread
	 * @param net a class implementing the NetCreator Interface
	 */
	public PetriNetEngine(int priority, int cycle_Time, PetriNet net) {
		super(priority, cycle_Time);
		// set up queues
		Active_Places        = new FixedSizeQueue<Place>(MAX_ACTIVE_PLACES);
		Possible_Transitions = new FixedSizeQueue<Transition>(MAX_ACTIVE_PLACES);
		
		StateMon             = new NetStateMonitor();
		
		__Net = net;
		// create the net
		__Net.createNet();
		
		// get starting place
		StartingPlace = __Net.getStartingPlace();
		StoppingPlace = __Net.getStoppingPlace();
		
		if (!__Net.checkNet()) {
			StateMon.setNetState(NetStateMonitor.STATE_ERROR);
		} 
	}
	
	/**
	 * Forwards a command to the monitor class
	 * @param command
	 */
	public void setCommand(byte command) {
		StateMon.setCommand(command);
	}
	
	/**
	 * Returns the state of the Net
	 * @return
	 */
	public byte getNetState() {
		return StateMon.getNetState();
	}
	
	/**
	 * Cyclic executed task
	 */
	protected void cyclicTask() {
		byte NetState = getNetState();
		//LCD.drawInt((int) NetState, 4, 1);
		//LCD.drawInt((int) Active_Places.size(), 6, 1);
		if (NetState == NetStateMonitor.STATE_STOPPED) {           /////////////// Stopped
			// Do nothing
		} else if (NetState == NetStateMonitor.STATE_STARTING) {   /////////////// Starting
			// if no starting place set -> Error!
			if (this.startNet()) {
				StateMon.setNetState(NetStateMonitor.STATE_RUNNING);
			} else {
				StateMon.setNetState(NetStateMonitor.STATE_ERROR);
			}
		} else if (NetState == NetStateMonitor.STATE_START_1_CYC) { /////////////// Starting single cycle run
			if (this.startNet()) {
				StateMon.setNetState(NetStateMonitor.STATE_RUN_TO_STOP);
			}
		} else if (NetState == NetStateMonitor.STATE_RUNNING) {     /////////////// Running
			this.runNet();
		} else if (NetState == NetStateMonitor.STATE_RUN_TO_STOP) {    /////////////// Run last cycle
			// In case the stopping place is active at the moment -> stop next time it becomes active
			// Needed if stopping = starting place
			if (StoppingPlace.isActive() && !Stopping_Place_was_deactivated)	{
				this.runNet();
			} else if (!StoppingPlace.isActive()) {
				// Stopping Place is deactivated: continue to run but remember this
				this.runNet();
				Stopping_Place_was_deactivated = true;
			} else {
				// Stopping place is active -> stop it!
				this.emergencyStop();
				StateMon.setNetState(NetStateMonitor.STATE_STOPPED);
			}
		} else if (NetState == NetStateMonitor.STATE_QUIT) {        //////////////// Force quit/emergency stop
			this.emergencyStop();
			StateMon.setNetState(NetStateMonitor.STATE_STOPPED);
		} else if (NetState == NetStateMonitor.STATE_PAUSE) {        //////////////// Paused
			// Do nothing and wait for external activation
		} else {
			// ERROR!!
			// We have a problem...
			//LCD.drawChar('E', 8, 7);
		}
	}
	
	/**
	 * Run/Excecute the net (one round)
	 */
	private void runNet() {
		Place      place;
		Transition transition;
		if (Active_Places.empty()) {
			StateMon.setNetState(NetStateMonitor.STATE_ERROR);
		}
		//LCD.drawChar('R', 3, 7);
		Active_Places.push(null); // insert null to recognize end of old places
		place = (Place) Active_Places.pop();
		// Iterate once over the queue (until the last, the null element is reached)
		while (place != null) {
			// If place hasn't been deactivated
			if (place.isActive()) {
				//LCD.drawChar('P', 4, 7);
				// Execute Place action
				place.runPlace();
				// Put place back into queue
				Active_Places.push(place);
				// Insert transitions into transition queue
				Possible_Transitions.pushArray(place.getTransitions());
			}
			// get next place
			place = (Place) Active_Places.pop();
		}
		//LCD.drawInt((int) Possible_Transitions.size(), 8, 1);
		// Check all possibly firing transitions
		while (!Possible_Transitions.empty()) {
			transition = Possible_Transitions.pop();
			//LCD.drawChar('T', 5, 7);
			// If transition fires
			if(transition.fireIfPossible()) {
				//LCD.drawChar('F', 5, 7);
				// push outgoing places onto the queue
				Active_Places.pushArray(transition.getPlaces_Out());
			}
		}
	}
	
	/**
	 * Start/reset the net
	 * @return success
	 */
	private boolean startNet() {
		//LCD.drawChar('S', 1, 7);
		// if no starting place set -> Error!
		if (StartingPlace == null) {
			return false;
		} else {
			// Reset last cycle flag and empty queues
			Stopping_Place_was_deactivated = false;
			Active_Places.clear();
			Possible_Transitions.clear();
			// Activate starting place
			StartingPlace.Activate();
			// insert starting place
			Active_Places.push(StartingPlace);
			// go and run!
			//LCD.drawChar('G', 2, 7);
			return true;
		}
	}
	
	private void emergencyStop() {
		Place      place;
		while (! Active_Places.empty()) {
			place = (Place) Active_Places.pop();
			place.emergencyExit();
		}		
	}
}
