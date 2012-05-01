package robot.TestNet;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import piaf.petrinet.NetCreator;
import piaf.petrinet.Place;
import piaf.petrinet.PlaceActions;
import piaf.petrinet.Transition;
import piaf.petrinet.TransitionCondition;

/**
 * @author mb
 *
 */
public class TestNetCreator extends NetCreator {
	
	
	public TestNetCreator() {

	}
	
/****************************** Places *****************************/
	public class PlayStartSound implements PlaceActions {

		@Override
		public void actionOnEntry() {
			LCD.drawInt(1, 2, 5);
			LCD.drawChar('A', 2, 2);
		}

		@Override
		public void actionWhileActive() {	
			LCD.drawChar('W', 2, 2);
		}

		@Override
		public void actionExit() {
			LCD.drawChar('D', 2, 2);
		}

		@Override
		public void stopAction() {
			LCD.drawChar('S', 2, 2);
			
		}
		
	}
	
	public class MoveLeftWheel implements PlaceActions {

		@Override
		public void actionOnEntry() {
			LCD.drawInt(2, 2, 5);
			LCD.drawChar('A', 7, 2);
			
		}

		@Override
		public void actionWhileActive() {
			LCD.drawChar('W', 7, 2);
			
		}

		@Override
		public void actionExit() {
			LCD.drawChar('D', 7, 2);
			
		}

		@Override
		public void stopAction() {
			LCD.drawChar('S', 7, 2);
		}
	}
	
/****************************** Transitions *****************************/
	
	public class SoundHasFinished implements TransitionCondition {

		@Override
		public boolean condition() {
			// TODO Auto-generated method stub
			return true;
		}
	}
	
	public class MovementDone implements TransitionCondition {
		
		/**
		 * True if none Motor is moving
		 */
		@Override
		public boolean condition() {
			return true;
		}
	}
	
/****************************** Connections *****************************/
	
	/* (non-Javadoc)
	 * @see piaf.NetCreator#createNet()
	 */
	@Override
	public boolean createNet() {
		boolean success = true;
		Places      = new Place[2];
		Transitions = new Transition[2];
		
		Places[0] = new Place((byte) 1, this.new PlayStartSound());
		Places[1] = new Place((byte) 1, this.new MoveLeftWheel());
		
		Transitions[0] = new Transition((byte) 1, (byte) 1, this.new SoundHasFinished());
		Transitions[1] = new Transition((byte) 1, (byte) 1, this.new MovementDone());
		
		success &= connect(Places[0], Transitions[0]);
		success &= connect(Transitions[0], Places[1]);
		success &= connect(Places[1], Transitions[1]);
		success &= connect(Transitions[1], Places[0]);
		
		return success;
	}

	/* (non-Javadoc)
	 * @see piaf.NetCreator#getStartingPlace()
	 */
	@Override
	public Place getStartingPlace() {
		return Places[0];
	}
	
	public void TestMe() {
		Place place;
		TouchSensor TestSensor = new TouchSensor(SensorPort.S2);
		place = getStartingPlace();
		while(!TestSensor.isPressed()) {
			Sound.beep();
			place = TestPlace(place);
		}

		
		
		
	}
	
	private Place TestPlace(Place place) {
		Transition[] transitions;
		Place[]  places;
		place.Activate();
		Button.waitForAnyPress();
		place.runPlace();
		Button.waitForAnyPress();
		place.Deactivate();
		Button.waitForAnyPress();
		transitions = place.getTransitions();
		places = transitions[0].getPlaces_Out();
		return places[0];
	}
	
	
	
	
	
	
	

}
