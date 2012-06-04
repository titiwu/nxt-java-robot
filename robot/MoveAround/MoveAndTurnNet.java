package robot.MoveAround;

import java.util.Random;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import piaf.petrinet.PetriNet;
import piaf.petrinet.PlaceActions;
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
		WheelLeft.setAcceleration(2000);
		WheelRight.setAcceleration(2000);
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
			WheelLeft.stop(true);
			WheelRight.stop(true);
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
	protected void buildNetStructure(NetCreator Net) {
		
		// Create Instances
		PlaceID P_Move      = Net.CreatePlace(new Move());
		PlaceID P_TurnRight = Net.CreatePlace(new TurnRight());
		PlaceID P_TurnLeft  = Net.CreatePlace(new TurnLeft());
		
		TransitionID T_TouchLeft     = Net.CreateTransition(new WallTouchedLeft()); 
		TransitionID T_TouchRight    = Net.CreateTransition(new WallTouchedRight()); 
		TransitionID T_TouchMiddle   = Net.CreateTransition(new WallTouchedMiddle()); 
		TransitionID T_LeftTurnDone  = Net.CreateTransition(new TurnDone()); 
		TransitionID T_RightTurnDone = Net.CreateTransition(new TurnDone()); 
		
		// Connections
		Net.connect(P_Move, T_TouchLeft);
		Net.connect(T_TouchLeft, P_TurnLeft);
		Net.connect(P_Move, T_TouchRight);
		Net.connect(T_TouchRight, P_TurnRight);
		Net.connect(P_Move, T_TouchMiddle);
		Net.connect(T_TouchMiddle, P_TurnLeft);

		Net.connect(P_TurnRight, T_RightTurnDone);
		Net.connect(T_RightTurnDone, P_Move);
		Net.connect(P_TurnLeft, T_LeftTurnDone);
		Net.connect(T_LeftTurnDone, P_Move);
		
		setStartingPlace(P_Move);
		setStoppingPlace(P_Move);
	}
	
	public void stop() {
		WheelLeft.flt(true);
		WheelRight.flt(true);
	}

}
