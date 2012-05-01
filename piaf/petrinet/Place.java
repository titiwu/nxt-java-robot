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
	private int Last_Active_Duration;
	
	private int Last_Startup_Time;

	private byte Nr_Of_Transitions_Out;
	private Transition[] Transitions;
	
	private PlaceActions a;
	
	// Activity indication
	private boolean Active;
	
	/**
	 * Constructor
	 * @param nr_Of_Transitions_Out
	 * @param actions
	 */
	public Place(byte nr_Of_Transitions_Out, PlaceActions actions) {
		Nr_Of_Transitions_Out = nr_Of_Transitions_Out;
		Transitions           = new Transition[Nr_Of_Transitions_Out];
		a                     = actions;
	}
	
	/**
	 * Get how many ms the Transition was active the last time
	 * @return int
	 */
	public int getLast_Active_Duration() {
		return Last_Active_Duration;
	}
	
	/**
	 * Get the Transition array of the Place
	 * @return BaseTransition
	 */
	public Transition[] getTransitions() {
		return Transitions;
	}

	/**
	 * Insert transition connections of this place
	 * @param BaseTransisition transition
	 */
	public boolean setTransition(Transition transition) {
		byte i = 0;
		// Find next empty transition
		while ((Transitions[i] != null) && (i < Nr_Of_Transitions_Out)) {
			i += 1;
		}
		// Save connection or fail
		if (i < Nr_Of_Transitions_Out) {
			Transitions[i] = transition;
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
		return Active;
	}
	
	/**
	 * Activates the place (puts a mark on it)
	 */
	public void Activate() {
		Last_Startup_Time = (int) System.currentTimeMillis();
		a.actionOnEntry();
		Active = true;
	}
	
	/**
	 * Deactivates the places (removes a mark from it)
	 */
	public void Deactivate() {
		a.actionExit();
		Last_Active_Duration = (int) System.currentTimeMillis() - Last_Startup_Time;
		Active = false;
	}
	
	/**
	 * Executes the places actual action
	 */
	public void runPlace() {
		a.actionWhileActive();
	}
	
	public void emergencyExit() {
		a.stopAction();
		Active = false;
	}
	
}
