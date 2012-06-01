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
	protected boolean NetCreate_Success = true;
	
	/**
	 * Connect two PetriNet Elements
	 * @param from_Place
	 * @param to_Transition
	 * @return success of the connection
	 */
	protected boolean connect(Place from_Place, Transition to_Transition) {
		NetCreate_Success &= from_Place.setTransition(to_Transition);
		NetCreate_Success &= to_Transition.setPlace_In(from_Place);
		return NetCreate_Success;
	}
	
	/**
	 * Connect two PetriNet Elements
	 * @param from_Transition
	 * @param to_Place
	 * @return success of the connection
	 */
	protected boolean connect(Transition from_Transition, Place to_Place) {
		NetCreate_Success &= from_Transition.setPlace_Out(to_Place);
		return NetCreate_Success;
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
		return true;
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
