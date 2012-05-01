package piaf.petrinet;


/**
 * A NetCreator retrieves the net information 
 * (for example from an PNML file or a its static implementation)
 * and instantiates the places and transitions
 * @author mb
 *
 */
public abstract class NetCreator {
	protected Place[] Places;
	protected Transition[] Transitions;
	
	/**
	 * Connect two PetriNet Elements
	 * @param from_Place
	 * @param to_Transition
	 * @return success of the connection
	 */
	protected boolean connect(Place from_Place, Transition to_Transition) {
		boolean success = true; 
		success &= from_Place.setTransition(to_Transition);
		success &= to_Transition.setPlace_In(from_Place);
		return success;
	}
	
	/**
	 * Connect two PetriNet Elements
	 * @param from_Transition
	 * @param to_Place
	 * @return success of the connection
	 */
	protected boolean connect(Transition from_Transition, Place to_Place) {
		return from_Transition.setPlace_Out(to_Place);
	}
	
	/**
	 * Checks the net for possible errors
	 * @return
	 */
	protected boolean checkNet() {
		Place Starting_Place;
		Place[] Places[];
		Transition[] Transitions;
		Starting_Place = this.getStartingPlace();
		return false;
		//TODO
	}
	
	/**
	 * Creates the place and transition instances and 
	 * sets In- and Out-Places and Transitions
	 * them 
	 * @return success
	 */
	public abstract boolean createNet();
	
	/**
	 * Returns the starting Place
	 * @return Place to start
	 */
	public abstract Place getStartingPlace();
	
	

}
