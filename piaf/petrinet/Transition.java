package piaf.petrinet;


/**
 * @author mb
 * This class is the base used for transitions in a PetriNet
 * Each transition has a firing condition.
 * It has to be implemented by derived classes.
 * 
 */
public class Transition {
	private byte Nr_Of_Places_In;
	private byte Nr_Of_Places_Out;
	
	private Place[] Places_Out;
	private Place[] Places_In;
	
	private TransitionCondition c;

	/**
	 * @param nr_Of_Places_In
	 * @param nr_Of_Places_Out
	 */
	public Transition(byte nr_Of_Places_In, byte nr_Of_Places_Out, TransitionCondition condition) {
		super();
		this.Nr_Of_Places_In  = nr_Of_Places_In;
		this.Nr_Of_Places_Out = nr_Of_Places_Out;
		this.Places_In        = new Place[Nr_Of_Places_In];
		this.Places_Out       = new Place[Nr_Of_Places_Out];
		this.c                = condition;
	}

	/**
	 * Insert incoming place connections of this place
	 * @param Place place_in
	 */
	public boolean setPlace_In(Place place_in) {
		byte i = 0;
		// Find next empty transition
		while ((Places_In[i] != null) && (i < Nr_Of_Places_In)) {
			i += 1;
		}
		// Save connection or fail
		if (i < Nr_Of_Places_In) {
			Places_In[i] = place_in;
			return true;
		} else {
			return false;
		}	
	}
	
	/**
	 * Insert incoming place connections of this place
	 * @param Place place_in
	 */
	public boolean setPlace_Out(Place place_out) {
		byte i = 0;
		// Find next empty transition
		while ((Places_Out[i] != null) && (i < Nr_Of_Places_Out)) {
			i += 1;
		}
		// Save connection or fail
		if (i < Nr_Of_Places_Out) {
			Places_Out[i] = place_out;
			return true;
		} else {
			return false;
		}	
	}
	
	/**
	 * Returns the outgoing places
	 * @return
	 */
	public Place[] getPlaces_Out() {
		return this.Places_Out;
	}
	
	/**
	 * Fires if possible
	 * @return has fired?
	 */
	public boolean fireIfPossible() {
		// Not all of input places are active
		if (!this.AllInPlacesActive()) { 
			//LCD.drawChar('A', 4, 4);
			return false;
		// Firing condition is not met
		} else if (! c.condition()) {
			//LCD.drawChar('C', 4, 4);
			return false;
		// Fire!
		} else {
			//LCD.drawChar('F', 4, 4);
			this.deactivateIncomingPlaces();
			this.activateOutgoingPlaces();
			return true;
		}	
	}
	
	/**
	 * Tests if all incoming places are active
	 * @return
	 */
	private boolean AllInPlacesActive() {
		boolean all_active = true;
		for (byte i = 0; i < this.Places_In.length; i++) {
			all_active = all_active && this.Places_In[i].isActive();
		}
		return all_active;
	}
	
	/**
	 * Activates all outgoing places
	 */
	private void activateOutgoingPlaces() {
		for (byte i = 0; i < this.Places_Out.length; i++) {
			this.Places_Out[i].Activate();
		}
	}
	
	/**
	 * Deactivates all incoming places
	 */
	private void deactivateIncomingPlaces() {
		for (byte i = 0; i < this.Places_In.length; i++) {
			this.Places_In[i].Deactivate();
		}
	}
}
