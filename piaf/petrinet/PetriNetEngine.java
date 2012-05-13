package piaf.petrinet;

import piaf.threads.CyclicThread;
import piaf.tools.FixedSizeQueue;
import lejos.nxt.LCD;
import lejos.nxt.Sound;

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
	
	private Place Starting_Place;
	private boolean Starting_Place_was_deactivated;
	
	private FixedSizeQueue<Place>      Active_Places;
	private FixedSizeQueue<Transition> Possible_Transitions;
	
	private NetCreator nc;

	/**
	 * Constructor
	 *
	 * @param priority
	 * @param cycle_Time Cycle Time of the PetriNet Thread
	 * @param net a class implementing the NetCreator Interface
	 */
	public PetriNetEngine(int priority, int cycle_Time, NetCreator net) {
		super(priority, cycle_Time);
		// set up queues
		Active_Places        = new FixedSizeQueue<Place>(MAX_ACTIVE_PLACES);
		Possible_Transitions = new FixedSizeQueue<Transition>(MAX_ACTIVE_PLACES);
		
		StateMon             = new NetStateMonitor();
		
		nc = net;
		// create the net
		nc.createNet();
		
		// get starting place
		Starting_Place = nc.getStartingPlace();
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
		LCD.drawInt((int) NetState, 4, 1);
		LCD.drawInt((int) Active_Places.size(), 6, 1);
		if (NetState == NetStateMonitor.STATE_STOPPED) {           /////////////// Stopped
			// Do nothing
		} else if (NetState == NetStateMonitor.STATE_STARTING) {   /////////////// Starting
			// if no starting place set -> Error!
			if (this.startNet()) {
				StateMon.setNetState(NetStateMonitor.STATE_RUNNING);
			}
		} else if (NetState == NetStateMonitor.STATE_START_1_CYC) { /////////////// Starting single cycle run
			if (this.startNet()) {
				StateMon.setNetState(NetStateMonitor.STATE_LAST_CYC);
			}
		} else if (NetState == NetStateMonitor.STATE_RUNNING) {     /////////////// Running
			this.runNet();
		} else if (NetState == NetStateMonitor.STATE_LAST_CYC) {    /////////////// Run last cycle
			// Starting place still active or already deactivated
			if(( Starting_Place_was_deactivated && !Starting_Place.isActive()) ||
			   (!Starting_Place_was_deactivated &&  Starting_Place.isActive())) {
				this.runNet();
			// Starting place became deactivated
			} else if(!Starting_Place_was_deactivated && !Starting_Place.isActive()) {
				LCD.drawChar('L', 6, 7);
				this.runNet();
				Starting_Place_was_deactivated = true;
			// Starting place again active -> Cycle end reached!
			} else if(Starting_Place_was_deactivated && Starting_Place.isActive()) { 
				// Stop all possible ongoing movements
				LCD.drawChar('Q', 7, 7);
				StateMon.setNetState(NetStateMonitor.STATE_QUIT);
			}
		} else if (NetState == NetStateMonitor.STATE_QUIT) {        //////////////// Force quit/emergency stop
			this.emergencyStop();
			StateMon.setNetState(NetStateMonitor.STATE_STOPPED);
		} else {
			// We have a problem...
			LCD.drawChar('E', 8, 7);
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
		LCD.drawChar('R', 3, 7);
		Active_Places.push(null); // insert null to recognize end of old places
		place = (Place) Active_Places.pop();
		// Iterate once over the queue (until the last, the null element is reached)
		while (place != null) {
			// If place hasn't been deactivated
			if (place.isActive()) {
				LCD.drawChar('P', 4, 7);
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
		LCD.drawInt((int) Possible_Transitions.size(), 8, 1);
		// Check all possibly firing transitions
		while (!Possible_Transitions.empty()) {
			transition = Possible_Transitions.pop();
			LCD.drawChar('T', 5, 7);
			// If transition fires
			if(transition.fireIfPossible()) {
				LCD.drawChar('F', 5, 7);
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
		LCD.drawChar('S', 1, 7);
		// if no starting place set -> Error!
		if (Starting_Place == null) {
			StateMon.setNetState(NetStateMonitor.STATE_ERROR);
			return false;
		} else {
			// Reset last cycle flag and empty queues
			Starting_Place_was_deactivated = false;
			Active_Places.clear();
			Possible_Transitions.clear();
			// Activate starting place
			Starting_Place.Activate();
			// insert starting place
			Active_Places.push(Starting_Place);
			// go and run!
			LCD.drawChar('G', 2, 7);
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
