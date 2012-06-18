package piaf.petrinet;


/**
 * @author mb
 * This class is the base used for places in a PetriNet
 * Each place has an action at entry, while it is active
 * and on exit.
 * These have to be implemented by derived classes
 */
public class Place {
	// Times
	private int __last_active_duration;
	
	private int __last_startup_time;

	private byte __nr_of_transitions_out;

	private Transition[] __transitions;
	
	private PlaceActions __a;
	
	// Activity indication
	private boolean __active;
	
	public byte getNrOfTransitionsOut() {
		return __nr_of_transitions_out;
	}
	
	/**
	 * Constructor
	 * @param nr_of_transitions_out
	 * @param actions
	 */
	public Place(byte nr_of_transitions_out, PlaceActions actions) {
		__nr_of_transitions_out = nr_of_transitions_out;
		__transitions           = new Transition[__nr_of_transitions_out];
		__a                     = actions;
	}
	
	/**
	 * Get how many ms the transition was active the last time
	 * @return int
	 */
	public int getLastActiveDuration() {
		return __last_active_duration;
	}
	
	/**
	 * Get the Transition array of the Place
	 * @return BaseTransition
	 */
	public Transition[] getTransitions() {
		return __transitions;
	}

	/**
	 * Insert transition connections of this place
	 * @param BaseTransisition transition
	 */
	public boolean setTransition(Transition transition) {
		int i = 0;
		// Find next empty transition
		while ((__transitions[i] != null) && (i < __nr_of_transitions_out)) {
			i += 1;
		}
		// Save connection or fail
		if (i < __nr_of_transitions_out) {
			__transitions[i] = transition;
			return true;
		} else {
			return false;
		}	
	}
	
	/**
	 * Is the place at the moment active?
	 * @return boolean
	 */
	public boolean isActive() {
		return __active;
	}
	
	/**
	 * Activates the place (puts a token on it)
	 */
	public void activate() {
		__last_startup_time = (int) System.currentTimeMillis();
		__a.actionOnEntry();
		__active = true;
	}
	
	/**
	 * Deactivates the places (removes a token from it)
	 */
	public void deactivate() {
		__a.actionExit();
		__last_active_duration = (int) System.currentTimeMillis() - __last_startup_time;
		__active = false;
	}
	
	/**
	 * Executes the places actual action
	 */
	public void runPlace() {
		__a.actionWhileActive();
	}
	
	public void emergencyExit() {
		__a.stopAction();
		__active = false;
	}
	
}
