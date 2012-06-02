package piaf.petrinet;


/**
 * This class is intended to provide a simple construction of a 
 * Petri Net structure. It contains functions to simplify the creation 
 * of connections, test the net and is as well the interface for 
 * to the PetriNetEngine.
 * 
 * Subclasses of PetriNet contain the net information, its structure, 
 * actions and transition conditions
 * and the instances the places and transitions
 * 
 * A possibility is to generate a skeleton of a subclass 
 * from an PNML, or even generate the net by paring such a 
 * file at runtime.
 * 
 * @author mb
 *
 */
public abstract class PetriNet {
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
	 * Connects two places via a transition
	 * @param from_Place
	 * @param over_Transition
	 * @param to_Place
	 * @return
	 */
	protected boolean connect(Place from_Place, Transition over_Transition, Place to_Place) {
		connect(from_Place, over_Transition);
		connect(over_Transition, to_Place);
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
	 * Creates NetStructure and returns true
	 * if the creation was successful
	 * @return success
	 */
	public boolean createNet() {
		buildNetStructure();
		return NetCreate_Success;
	}
	
	/**
	 * Create instances of the places and transitions.
	 * 
	 * Connect the places and transitions using the
	 * provided "connect" function.
	 * 
	 * MyPlace1 = new Place((byte) 1, new myPlaceAction1());
	 * MyPlace2 = new Place((byte) 1, new myPlaceAction2());
	 * MyTransition = new Transition((byte) 1, (byte) 1, new myTransitionCondition());
	 * 
	 * connect(MyPlace1, Transition);
	 * connect(Transition, MyPlace);
	 * 
	 */
	protected abstract void buildNetStructure();
	
	/**
	 * Returns the starting Place
	 * @return Place to start
	 */
	public abstract Place getStartingPlace();
	
	

}
