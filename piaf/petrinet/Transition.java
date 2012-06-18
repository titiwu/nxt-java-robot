package piaf.petrinet;


/**
 * @author mb
 * This class is the base used for transitions in a PetriNet
 * Each transition has a firing condition.
 * It has to be implemented by derived classes.
 * 
 */
public class Transition {
	private byte __nr_of_places_in;
	private byte __nr_of_places_out;
	
	private Place[] __places_out;
	private Place[] __places_in;
	
	private TransitionCondition __c;

	public byte getNrOfPlacesIn() {
		return __nr_of_places_in;
	}

	public byte getNrOfPlacesOut() {
		return __nr_of_places_out;
	}
	
	/**
	 * @param nr_of_places_in
	 * @param nr_of_places_out
	 */
	public Transition(byte nr_of_places_in, byte nr_of_places_out, TransitionCondition condition) {
		super();
		__nr_of_places_in  = nr_of_places_in;
		__nr_of_places_out = nr_of_places_out;
		__places_in        = new Place[__nr_of_places_in];
		__places_out       = new Place[__nr_of_places_out];
		__c                = condition;
	}

	/**
	 * Insert incoming place connections of this place
	 * @param Place place_in
	 */
	public boolean setPlaceIn(Place place_in) {
		int i = 0;
		// Find next empty transition
		while ((__places_in[i] != null) && (i < __nr_of_places_in)) {
			i += 1;
		}
		// Save connection or fail
		if (i < __nr_of_places_in) {
			__places_in[i] = place_in;
			return true;
		} else {
			return false;
		}	
	}
	
	/**
	 * Insert incoming place connections of this place
	 * @param Place place_in
	 */
	public boolean setPlaceOut(Place place_out) {
		int i = 0;
		// Find next empty transition
		while ((__places_out[i] != null) && (i < __nr_of_places_out)) {
			i += 1;
		}
		// Save connection or fail
		if (i < __nr_of_places_out) {
			__places_out[i] = place_out;
			return true;
		} else {
			return false;
		}	
	}
	
	/**
	 * Returns the outgoing places
	 * @return
	 */
	public Place[] getPlacesOut() {
		return this.__places_out;
	}
	
	/**
	 * Fires if possible
	 * @return has fired?
	 */
	public boolean fireIfPossible() {
		// Not all of input places are active
		if (!this.allInPlacesActive()) { 
			//LCD.drawChar('A', 4, 4);
			return false;
		// Firing condition is not met
		} else if (! __c.condition()) {
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
	private boolean allInPlacesActive() {
		boolean all_active = true;
		for (int i = 0; i < __places_in.length; i++) {
			all_active &= __places_in[i].isActive();
		}
		return all_active;
	}
	
	/**
	 * Activates all outgoing places
	 */
	private void activateOutgoingPlaces() {
		for (int i = 0; i < __places_out.length; i++) {
			this.__places_out[i].activate();
		}
	}
	
	/**
	 * Deactivates all incoming places
	 */
	private void deactivateIncomingPlaces() {
		for (int i = 0; i < __places_in.length; i++) {
			this.__places_in[i].deactivate();
		}
	}
}
