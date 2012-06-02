package robot.MoveAround;

import java.util.Random;

import lejos.nxt.Motor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import piaf.petrinet.PetriNet;
import piaf.petrinet.Place;
import piaf.petrinet.PlaceActions;
import piaf.petrinet.Transition;
import piaf.petrinet.TransitionCondition;
import robot.Sound.SoundPlayer;

public class MoveAndTurnNet  extends PetriNet {
	private NXTRegulatedMotor WheelLeft;
	private NXTRegulatedMotor WheelRight;
	private SoundPlayer       SoundMgr;
	private TouchSensor       TouchLeft;
	private TouchSensor       TouchRight;
	private Random            RandomNr;
	
	public final static int Speed = 360;
	
	public MoveAndTurnNet() {
		WheelRight = Motor.A;
		WheelLeft  = Motor.B;
		SoundMgr   = SoundPlayer.getInstance();
		TouchRight = new TouchSensor(SensorPort.S1);
		TouchLeft  = new TouchSensor(SensorPort.S2);
		RandomNr   = new Random();
		WheelLeft.setSpeed(Speed);
		WheelRight.setSpeed(Speed);
		WheelLeft.setAcceleration(3000);
		WheelRight.setAcceleration(3000);
	}

	/****************************** Places *****************************/
	public class Move implements PlaceActions {

		@Override
		public void actionOnEntry() {
			WheelLeft.backward();
			WheelRight.backward();
		}

		@Override
		public void actionWhileActive() {
		}

		@Override
		public void actionExit() {
			stopAction();
			SoundMgr.putCommand(SoundPlayer.DoubleBeep);
		}

		@Override
		public void stopAction() {
			WheelLeft.flt(true);
			WheelRight.flt(true);
		}
		
	}
	
	public class TurnRight implements PlaceActions {

		@Override
		public void actionOnEntry() {
			int TurnAngle = 300-RandomNr.nextInt(200);
			WheelLeft.rotate(TurnAngle, true);
			WheelRight.rotate(-TurnAngle, true);
		}

		@Override
		public void actionWhileActive() {
			
		}

		@Override
		public void actionExit() {
			SoundMgr.putCommand(SoundPlayer.Beep);
		}

		@Override
		public void stopAction() {
			WheelLeft.flt(true);
			WheelRight.flt(true);
		}
		
	}
	
	public class TurnLeft implements PlaceActions {

		@Override
		public void actionOnEntry() {
			int TurnAngle = 300-RandomNr.nextInt(200);
			WheelLeft.rotate(-TurnAngle, true);
			WheelRight.rotate(TurnAngle, true);
		}

		@Override
		public void actionWhileActive() {
		}

		@Override
		public void actionExit() {
			SoundMgr.putCommand(SoundPlayer.Buzz);
		}

		@Override
		public void stopAction() {
			WheelLeft.flt(true);
			WheelRight.flt(true);
			
		}
		
	}
	
	/****************************** Transitions *****************************/
	
	public class WallTouchedLeft implements TransitionCondition {

		@Override
		public boolean condition() {
			return TouchLeft.isPressed() && !TouchRight.isPressed();
		}
	}
	
	public class WallTouchedRight implements TransitionCondition {

		@Override
		public boolean condition() {
			return TouchRight.isPressed() && !TouchLeft.isPressed();
		}
	}
	
	public class WallTouchedMiddle implements TransitionCondition {

		@Override
		public boolean condition() {
			return TouchRight.isPressed() && TouchLeft.isPressed();
		}
	}
	
	public class TurnDone implements TransitionCondition {
		/**
		 * True if no Motor is moving
		 */
		@Override
		public boolean condition() {
			return (!WheelRight.isMoving() && !WheelLeft.isMoving());			
		}
	}
	
	
	/****************************** Connections *****************************/
	
	@Override
	protected void buildNetStructure() {
		Places      = new Place[3];
		Transitions = new Transition[5];
		
		// Create Instances
		Places[1] = new Place((byte) 3, new Move());
		Places[1] = new Place((byte) 1, new TurnRight());
		Places[2] = new Place((byte) 1, new TurnLeft());
		
		Transitions[0] = new Transition((byte) 1, (byte) 1, new WallTouchedLeft()); 
		Transitions[1] = new Transition((byte) 1, (byte) 1, new WallTouchedRight()); 
		Transitions[2] = new Transition((byte) 1, (byte) 1, new WallTouchedMiddle()); 
		Transitions[3] = new Transition((byte) 1, (byte) 1, new TurnDone()); 
		Transitions[4] = new Transition((byte) 1, (byte) 1, new TurnDone()); 
		
		connect(Places[0], Transitions[0], Places[2]);
		connect(Places[0], Transitions[1], Places[1]);
		connect(Places[0], Transitions[2], Places[2]);
		
		connect(Places[1], Transitions[3], Places[0]);	
		
		connect(Places[2], Transitions[4], Places[0]);
		
	}
	
	@Override
	public Place getStartingPlace() {
		return Places[0];
	}
	
	public void stop() {
		WheelLeft.flt(true);
		WheelRight.flt(true);
	}



}
