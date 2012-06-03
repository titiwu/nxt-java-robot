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
	private   int     Nr_of_Places      = 0;
	private   int     Nr_of_Transitions = 0;
	private   int     PlaceCounter      = 0;
	private   int     TransitionCounter = 0;
	
	/*
	private interface NetCreator {
		abstract int CreatePlace(PlaceActions actions);
		abstract int CreateTransiton(TransitionCondition condition);
		
		abstract boolean connect(int from_Place_ID, int via_Transition_ID, int to_Place_ID);
	}
	
	private class CountPlacesAndTransitions implements NetCreator {

		@Override
		public int CreatePlace(PlaceActions actions) {
			Nr_of_Places += 1;
			return Nr_of_Places -1;
		}

		@Override
		public int CreateTransiton(TransitionCondition condition) {
			Nr_of_Transitions += 1;
			return Nr_of_Transitions -1;
		}

		@Override
		public boolean connect(int from_Place_ID, int via_Transition_ID, int to_Place_ID) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	private class CreatePlacesAndTransitions implements NetCreator {
		private int PlaceCounter      = 0;
		private int TransitionCounter = 0;
		
		@Override
		public int CreatePlace(PlaceActions actions) {
			Places[PlaceCounter] = new Place((byte) 1, actions);
			return Nr_of_Places -1;
		}

		@Override
		public int CreateTransiton(TransitionCondition condition) {
			Nr_of_Transitions += 1;
			return Nr_of_Transitions -1;
		}

		@Override
		public boolean connect(int from_Place_ID, int via_Transition_ID, int to_Place_ID) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	*/
	
	protected void setSizes(int nr_of_places, int nr_of_transitions) {
		Places      = new Place[nr_of_places];
		Transitions = new Transition[nr_of_transitions];
	}
	
	
	/**
	 * Creates an instance for the place and returns the ID of the new 
	 * created place which is used for connections.
	 * 
	 * @param Nr_of_transitions_out
	 * @param actions - an instance implementing PlaceActions
	 * @return int Place_ID
	 */
	protected int CreatePlace(int Nr_of_transitions_out, PlaceActions actions) {
		Places[PlaceCounter] = new Place((byte) Nr_of_transitions_out, actions);
		PlaceCounter += 1;
		return PlaceCounter - 1;
	}
	
	/**
	 * Creates an instance for the transition and returns the ID of the new 
	 * created transition which is used for connections
	 * @param Nr_of_places_out
	 * @param Nr_of_places_in
	 * @param condtion - an instance of TransitionCondition
	 * @return int Transition_ID
	 */
	protected int CreateTransition(int Nr_of_places_out, int Nr_of_places_in, TransitionCondition condtion) {
		Transitions[TransitionCounter] = new Transition((byte) Nr_of_places_out, (byte) Nr_of_places_out, condtion);
		TransitionCounter += 1;
		return TransitionCounter - 1;
	}
	
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
	 * Connects two places via a transition using their IDs
	 * @param from_Place
	 * @param over_Transition
	 * @param to_Place
	 * @return
	 */
	protected boolean connect(int from_Place_ID, int via_Transition_ID, int to_Place_ID) {
		connect(Places[from_Place_ID], Transitions[via_Transition_ID]);
		connect(Transitions[via_Transition_ID], Places[to_Place_ID]);
		return NetCreate_Success;
	}
	
	/**
	 * Checks the net for possible errors
	 * @return
	 */
	protected boolean checkNet() {
		Place Starting_Place;
		Place Stopping_Place;
		
		Place[] Places[];
		Transition[] Transitions;
		
		Starting_Place = this.getStartingPlace();
		
		if (Starting_Place == null) {
			return false; // No starting place set!
		}
		
		Stopping_Place = this.getStoppingPlace();
		
		if (Stopping_Place == null) {
			return false; // No stopping place set!
		}
		
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
	
	/**
	 * Returns the stopping Place (End place)
	 * @return Place to stop at
	 */
	public abstract Place getStoppingPlace();

}
