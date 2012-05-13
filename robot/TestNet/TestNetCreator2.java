package robot.TestNet;

import lejos.nxt.LightSensor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import piaf.petrinet.NetCreator;
import piaf.petrinet.Place;
import piaf.petrinet.PlaceActions;
import piaf.petrinet.Transition;
import piaf.petrinet.TransitionCondition;
import piaf.tools.SimpleTimer;
import robot.Sound.SoundPlayer;

/**
 * @author mb
 *
 */
public class TestNetCreator2 extends NetCreator {
	
	private NXTRegulatedMotor LeftWheel;
	private NXTRegulatedMotor RightWheel;
	private SoundPlayer       Sound;
	private TouchSensor       Touch;
	private LightSensor       Light;
	
	public TestNetCreator2(SoundPlayer sound) {
		LeftWheel  = new NXTRegulatedMotor(MotorPort.B);
		RightWheel = new NXTRegulatedMotor(MotorPort.A);
		Sound      = sound;
		Touch      = new TouchSensor(SensorPort.S1);
		Light      = new LightSensor(SensorPort.S2);
	}
	
/****************************** Places *****************************/
	public class PlayStartSound implements PlaceActions {
		@Override
		public void actionOnEntry() {
			Sound.putCommand(SoundPlayer.StartSound);
		}

		@Override
		public void actionWhileActive() {	
			
		}

		@Override
		public void actionExit() {
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
	
	public class MoveRightWheel implements PlaceActions {
		@Override
		public void actionOnEntry() {
			RightWheel.rotate(180, true);
			
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
			RightWheel.stop(true);
		}
	}	
	
	public class SwitchLightOn implements PlaceActions {
		@Override
		public void actionOnEntry() {
			Light.setFloodlight(true);
			
		}

		@Override
		public void actionWhileActive() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void actionExit() {
			Light.setFloodlight(false);
			
		}

		@Override
		public void stopAction() {
			Light.setFloodlight(false);
		}
	}
	
/****************************** Transitions *****************************/
	
	/**
	 * Wait 1 Second before continuing
	 * @author mb
	 *
	 */
	public class SoundHasFinished implements TransitionCondition {
		SimpleTimer WaitTimer = new SimpleTimer(1000);
		@Override
		public boolean condition() {
			if(WaitTimer.isStarted()) {
				WaitTimer.startTimer();
			}
			if(WaitTimer.timedOut()) {
				return true;
			} else {
				return false;
			}
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
	
	public class WaitForTouch implements TransitionCondition {
		
		/**
		 * True if touch sensor has been activated
		 */
		@Override
		public boolean condition() {
			return Touch.isPressed();
		}
	}
	
/****************************** Connections *****************************/
	
	/* (non-Javadoc)
	 * @see piaf.NetCreator#createNet()
	 */
	@Override
	public boolean createNet() {
		boolean success = true;
		Places      = new Place[4];
		Transitions = new Transition[3];
		
		// Instantiate Places and Conditions
		Places[0] = new Place((byte) 1, new PlayStartSound());
		Places[1] = new Place((byte) 1, new MoveRightWheel());
		Places[2] = new Place((byte) 1, new MoveLeftWheel());
		Places[3] = new Place((byte) 1, new SwitchLightOn());
		
		Transitions[0] = new Transition((byte) 1, (byte) 2, new SoundHasFinished());
		Transitions[1] = new Transition((byte) 2, (byte) 1, new MovementDone());
		Transitions[2] = new Transition((byte) 1, (byte) 1, new WaitForTouch());
		
		// Connect Net
		success &= connect(Places[0], Transitions[0]);		
		success &= connect(Transitions[0], Places[1]);
		success &= connect(Transitions[0], Places[2]);
		success &= connect(Places[1], Transitions[1]);
		success &= connect(Places[2], Transitions[1]);
		success &= connect(Transitions[1], Places[3]);
		success &= connect(Places[3], Transitions[2]);
		success &= connect(Transitions[2], Places[0]);
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
