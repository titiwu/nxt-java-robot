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
	
	private final static byte IN  = 0;
	private final static byte OUT = 1;
	
	
	protected Place[]      Places;
	protected Transition[] Transitions;
	
	protected boolean NetCreate_Success = true;
	
	private byte[]   Nr_of_out_transitions_for_place;
	private byte[][] Nr_of_in_out_places_for_transition;
	private int      Nr_of_Places      = 0;
	private int      Nr_of_Transitions = 0;
	private PlaceID  StartingPlace;
	private PlaceID  StoppingPlace;
	
	public class PlaceID {
		
		public int ID = -1;

		/**
		 * @param iD
		 */
		public PlaceID(int iD) {
			super();
			ID = iD;
		}
	}
	
	public class TransitionID {
		public int ID = -1;

		/**
		 * @param iD
		 */
		public TransitionID(int iD) {
			super();
			ID = iD;
		}
	}

	public interface NetCreator {
		abstract PlaceID CreatePlace(PlaceActions actions);
		abstract TransitionID CreateTransition(TransitionCondition condition);
		
		abstract boolean connect(PlaceID from_Place, TransitionID to_Transition);
		abstract boolean connect(TransitionID from_Transition, PlaceID to_Place);
	}
	
	private class CountPlacesAndTransitions implements NetCreator {

		@Override
		public PlaceID CreatePlace(PlaceActions actions) {
			Nr_of_Places += 1;
			return new PlaceID(Nr_of_Places -1);
		}

		@Override
		public TransitionID CreateTransition(TransitionCondition condition) {
			Nr_of_Transitions += 1;
			return new TransitionID(Nr_of_Transitions -1);
		}

		@Override
		public boolean connect(PlaceID from_Place, TransitionID to_Transition) {
			// Do nothing in here
			return true;
		}

		@Override
		public boolean connect(TransitionID from_Transition, PlaceID to_Place) {
			// Do nothing in here
			return true;
		}
		
	}
	
	private class CountConnections implements NetCreator {
		private int PlaceCounter      = 0;
		private int TransitionCounter = 0;
		
		/**
		 * Only returns the number of the place in order 
		 * to get the connections right
		 */
		@Override
		public PlaceID CreatePlace(PlaceActions actions) {
			PlaceCounter += 1;
			return new PlaceID(PlaceCounter - 1);
		}
		
		/**
		 * Only returns the number of the transition in order 
		 * to get the connections right
		 */
		@Override
		public TransitionID CreateTransition(TransitionCondition condition) {
			TransitionCounter += 1;
			return new TransitionID(TransitionCounter - 1);
		}

		/**
		 * Counts the connections for the places and transitions
		 * in order to allow static arrays for the links
		 */
		@Override
		public boolean connect(PlaceID from_Place, TransitionID to_Transition) {
			Nr_of_out_transitions_for_place[from_Place.ID]           += 1;
			Nr_of_in_out_places_for_transition[to_Transition.ID][IN] += 1;
			return true;
		}
		
		/**
		 * Counts the connections for the places and transitions
		 * in order to allow static arrays for the links
		 */
		@Override
		public boolean connect(TransitionID from_Transition, PlaceID to_Place) {
			Nr_of_in_out_places_for_transition[from_Transition.ID][OUT] += 1;
			return true;
		}
		
	}
	
	private class CreatePlacesAndTransitions implements NetCreator {
		private int PlaceCounter      = 0;
		private int TransitionCounter = 0;
		
		/**
		 * Creates an instance for the place and returns the ID of the new 
		 * created place which is used for connections.
		 * 
		 * @param actions - an instance implementing PlaceActions
		 * @return int Place_ID
		 */
		@Override
		public PlaceID CreatePlace(PlaceActions actions) {
			Places[PlaceCounter] = new Place((byte) Nr_of_out_transitions_for_place[PlaceCounter], actions);
			PlaceCounter += 1;
			return new PlaceID(PlaceCounter - 1);
		}
		
		/**
		 * Creates an instance for the transition and returns the ID of the new 
		 * created transition which is used for connections
		 * @param condtion - an instance of TransitionCondition
		 * @return int Transition_ID
		 */
		@Override
		public TransitionID CreateTransition(TransitionCondition condition) {
			Transitions[TransitionCounter] = new Transition( Nr_of_in_out_places_for_transition[TransitionCounter][0], 
															 Nr_of_in_out_places_for_transition[TransitionCounter][1], 
															 condition);
			TransitionCounter += 1;
			return new TransitionID(TransitionCounter - 1);
		}
		
		/**
		 * Sets the connections
		 */
		@Override
		public boolean connect(PlaceID from_Place, TransitionID to_Transition) {
			PetriNet.this.connect(Places[from_Place.ID], Transitions[to_Transition.ID]);
			return NetCreate_Success;
		}
		
		/**
		 * Sets the connections
		 */
		@Override
		public boolean connect(TransitionID from_Transition, PlaceID to_Place) {
			PetriNet.this.connect(Transitions[from_Transition.ID], Places[to_Place.ID]);
			return NetCreate_Success;
		}
		
	}

	/**
	 * Create the Places and Transition array
	 * and the helper arrays containing the number of connections
	 * @param nr_of_places
	 * @param nr_of_transitions
	 */
	protected void setSizes(int nr_of_places, int nr_of_transitions) {
		Places      = new Place[nr_of_places];
		Transitions = new Transition[nr_of_transitions];
		Nr_of_out_transitions_for_place    = new byte[nr_of_places];
		Nr_of_in_out_places_for_transition = new byte[nr_of_transitions][2];
		
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
	 * Checks the net for possible errors
	 * @return
	 */
	protected boolean checkNet() {
	
		// Valid starting place?
		if (StartingPlace.ID < 0 || StartingPlace.ID >= Nr_of_Places) {
			return false; // No/wrong starting place set!
		}
		
		// Valid stopping place?
		if (StoppingPlace.ID < 0 || StoppingPlace.ID >= Nr_of_Places) {
			return false; // No/wrong stopping place set!
		}
		
		// Any transition without in-place or out-place?
		for (Transition T : Transitions) {
		    if (T.getNr_Of_Places_Out() < 1 || T.getNr_Of_Places_In() < 1) {
		    	return false;
		    }
		}
		
		// Any place (that is not the stopping place)
		// without outgoing connection?
		for (Place P : Places) {
			if(P.getNr_Of_Transitions_Out() < 1 && !P.equals(Places[StoppingPlace.ID])) {
		    	return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Creates NetStructure and returns true
	 * if the creation was successful
	 * @return success
	 */
	public boolean createNet() {
		
		// Count how many places and transition are in the net
		buildNetStructure(new CountPlacesAndTransitions());
		//Create the arrays for them
		setSizes(Nr_of_Places, Nr_of_Transitions);
		// Count the connections for each place and transition
		buildNetStructure(new CountConnections());
		// Create the objects and connect places and transitions
		buildNetStructure(new CreatePlacesAndTransitions());
		
		return NetCreate_Success;
	}
	
	/**
	 * Implements the net structure
	 * 
	 * Connect the places and transitions using the
	 * provided "connect" function.
	 * 
	 * PlaceID      MyPlace1     = Net.CreatePlace(new myPlaceAction1());
	 * PlaceID      MyPlace2     = Net.CreatePlace(new myPlaceAction2());
	 * TransitionID MyTransition = Net.CreateTransition(new myTransitionCondition());
	 * 
	 * Net.connect(MyPlace1, Transition);
	 * Net.connect(Transition, MyPlace);
	 * 
	 * setStartingPlace(MyPlace1);
	 * setStoppingPlace(MyPlace2);
	 * 
	 */
	protected abstract void buildNetStructure(NetCreator Net);
	
	/**
	 * Returns the starting Place
	 * @return Place to start
	 */
	public Place getStartingPlace() {
		return Places[StartingPlace.ID];
	}
	
	/**
	 * Sets the ID of the starting place
	 * To be called in buildNetStructure
	 * @param starting_place
	 */
	protected void setStartingPlace(PlaceID starting_place) {
		StartingPlace = starting_place;
	}
	
	/**
	 * Returns the stopping Place (End place)
	 * @return Place to stop at
	 */
	public Place getStoppingPlace() {
		return Places[StoppingPlace.ID];
	}
	
	/**
	 * Sets the ID of the stopping place
	 * To be called in buildNetStructure
	 * @param starting_place
	 */
	protected void setStoppingPlace(PlaceID stopping_place) {
		StoppingPlace = stopping_place;
	}
}
