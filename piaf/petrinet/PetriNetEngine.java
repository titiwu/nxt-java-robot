package piaf.petrinet;

import piaf.tools.FixedSizeQueue;

/**
 * This is the implementation of a PetriNet engine.
 * The information about the net is kept in the place and transition classes
 * It executes the actions of the places and makes the transitions when their
 * condition is met.
 * 
 * @author mb
 *
 */
public class PetriNetEngine {

	private final static byte MAX_ACTIVE_PLACES = 8;
	
	private Place __starting_place;
	private Place __stopping_place;
	private boolean __stopping_place_was_deactivated;
	
	private FixedSizeQueue<Place>      __queue_active_places          = new FixedSizeQueue<Place>(MAX_ACTIVE_PLACES);
	private FixedSizeQueue<Transition> __queue_possible_transitions   = new FixedSizeQueue<Transition>(MAX_ACTIVE_PLACES);
	
	private PetriNetInterface __control;

	/**
	 * Constructor
	 * @param net     The PetriNet instance
	 * @param control A petri net control interface
	 */
	public PetriNetEngine(PetriNet net, PetriNetInterface control) {
		super();
		
		// Interface
		this.__control = control;
		
		// get starting place
		__starting_place = net.getStartingPlace();
		__stopping_place = net.getStoppingPlace();
		
		if (!net.checkNet()) {
			__control.setNetState(PetriNetInterface.STATE_ERROR);
		} 
	}
	
	/**
	 * Main function, executes the actions depending
	 * on the state and manipulates the state
	 */
	public void runEngine() {
		byte net_state = __control.getNetState();
		//LCD.drawInt((int) NetState, 4, 1);
		//LCD.drawInt((int) Active_Places.size(), 6, 1);
		if (net_state == PetriNetInterface.STATE_STOPPED) {           /////////////// Stopped
			// Do nothing
		} else if (net_state == PetriNetInterface.STATE_STARTING) {   /////////////// Starting
			// if no starting place set -> Error!
			if (this.startNet()) {
				__control.setNetState(PetriNetInterface.STATE_RUNNING);
			} else {
				__control.setNetState(PetriNetInterface.STATE_ERROR);
			}
		} else if (net_state == PetriNetInterface.STATE_START_1_CYC) { /////////////// Starting single cycle run
			if (this.startNet()) {
				__control.setNetState(PetriNetInterface.STATE_RUN_TO_STOP);
			}
		} else if (net_state == PetriNetInterface.STATE_RUNNING) {     /////////////// Running
			this.runNet();
		} else if (net_state == PetriNetInterface.STATE_RUN_TO_STOP) {    /////////////// Run last cycle
			// In case the stopping place is active at the moment -> stop next time it becomes active
			// Needed if stopping = starting place
			if (__stopping_place.isActive() && !__stopping_place_was_deactivated)	{
				this.runNet();
			} else if (!__stopping_place.isActive()) {
				// Stopping Place is deactivated: continue to run but remember this
				this.runNet();
				__stopping_place_was_deactivated = true;
			} else {
				// Stopping place is active -> stop it!
				this.stopNet();
				__control.setNetState(PetriNetInterface.STATE_STOPPED);
			}
		} else if (net_state == PetriNetInterface.STATE_QUIT) {        //////////////// Force quit/emergency stop
			this.stopNet();
			__control.setNetState(PetriNetInterface.STATE_STOPPED);
		} else if (net_state == PetriNetInterface.STATE_PAUSE) {        //////////////// Paused
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
		if (__queue_active_places.empty()) {
			__control.setNetState(PetriNetInterface.STATE_ERROR);
		}
		//LCD.drawChar('R', 3, 7);
		__queue_active_places.push(null); // insert null to recognize end of old places
		place = (Place) __queue_active_places.pop();
		// Iterate once over the queue (until the last, the null element is reached)
		while (place != null) {
			// If place hasn't been deactivated
			if (place.isActive()) {
				//LCD.drawChar('P', 4, 7);
				// Execute Place action
				place.runPlace();
				// Put place back into queue
				__queue_active_places.push(place);
				// Insert transitions into transition queue
				__queue_possible_transitions.pushArray(place.getTransitions());
			}
			// get next place
			place = (Place) __queue_active_places.pop();
		}
		//LCD.drawInt((int) Possible_Transitions.size(), 8, 1);
		// Check all possibly firing transitions
		while (!__queue_possible_transitions.empty()) {
			transition = __queue_possible_transitions.pop();
			//LCD.drawChar('T', 5, 7);
			// If transition fires
			if(transition.fireIfPossible()) {
				//LCD.drawChar('F', 5, 7);
				// push outgoing places onto the queue
				__queue_active_places.pushArray(transition.getPlacesOut());
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
		if (__starting_place == null) {
			return false;
		} else {
			// Reset last cycle flag and empty queues
			__stopping_place_was_deactivated = false;
			__queue_active_places.clear();
			__queue_possible_transitions.clear();
			// Activate starting place
			__starting_place.activate();
			// insert starting place
			__queue_active_places.push(__starting_place);
			// go and run!
			//LCD.drawChar('G', 2, 7);
			return true;
		}
	}
	
	/**
	 * Stop all active places
	 * (execute stop action)
	 */
	private void stopNet() {
		Place      place;
		while (! __queue_active_places.empty()) {
			place = (Place) __queue_active_places.pop();
			place.emergencyExit();
		}		
	}

}
