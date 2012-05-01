package robot.TestNet;

import lejos.nxt.LightSensor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import piaf.petrinet.NetCreator;
import piaf.petrinet.Place;
import piaf.petrinet.PlaceActions;
import piaf.petrinet.Transition;
import piaf.petrinet.TransitionCondition;

/**
 * @author mb
 *
 */
public class TestNetCreator2 extends NetCreator {
	
	private NXTRegulatedMotor LeftWheel;
	private NXTRegulatedMotor RightWheel;
	private LightSensor       SensorLight;
	
	public TestNetCreator2() {
		LeftWheel  = new NXTRegulatedMotor(MotorPort.B);
		RightWheel = new NXTRegulatedMotor(MotorPort.A);
		SensorLight = new LightSensor(SensorPort.S1);
	}
	
/****************************** Places *****************************/
	public class PlayStartSound implements PlaceActions {

		@Override
		public void actionOnEntry() {
			SensorLight.setFloodlight(true);
		}

		@Override
		public void actionWhileActive() {	
			
		}

		@Override
		public void actionExit() {
			SensorLight.setFloodlight(false);
		}

		@Override
		public void stopAction() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public class MoveLeftWheel implements PlaceActions {

		@Override
		public void actionOnEntry() {
			LeftWheel.rotate(180, true);
			
		}

		@Override
		public void actionWhileActive() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void actionExit() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void stopAction() {
			LeftWheel.stop(true);
		}
	}
	
/****************************** Transitions *****************************/
	
	public class SoundHasFinished implements TransitionCondition {

		@Override
		public boolean condition() {
			// TODO Auto-generated method stub
			return SensorLight.isFloodlightOn();
		}
	}
	
	public class MovementDone implements TransitionCondition {
		
		/**
		 * True if none Motor is moving
		 */
		@Override
		public boolean condition() {
			return (!RightWheel.isMoving() && !LeftWheel.isMoving());
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
		
		Places[0] = new Place((byte) 1, new PlayStartSound());
		Places[1] = new Place((byte) 1, new MoveLeftWheel());
		
		Transitions[0] = new Transition((byte) 1, (byte) 1, new SoundHasFinished());
		Transitions[1] = new Transition((byte) 1, (byte) 1, new MovementDone());
		
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
	
	
	
	
	
	
	
	

}
