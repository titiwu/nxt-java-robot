package robot;

import piaf.petrinet.NetStateMonitor;
import piaf.petrinet.PetriNetEngine;
import robot.Sound.SoundPlayer;
import robot.TestNet.TestNetCreator2;
import lejos.nxt.Button;


/**
  * This is the starting point for the robot-software
 * @author mb
 *
 */
public class C3LegO {


	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		
		
		SoundPlayer SoundThread = new SoundPlayer(Thread.NORM_PRIORITY);
		
		TestNetCreator2 TestNet = new TestNetCreator2(SoundThread);
		
		PetriNetEngine Engine   = new PetriNetEngine(Thread.NORM_PRIORITY, 300, TestNet);
		
		
		SoundThread.start();
	    Engine.start();
	    
	    Button.waitForAnyPress();
	    Engine.setCommand(NetStateMonitor.COMMAND_RUN);
	    SoundThread.putCommand(SoundPlayer.Ascending);
	    Button.waitForAnyPress();
	    Engine.setCommand(NetStateMonitor.COMMAND_STOP_CYC_END);
	    SoundThread.putCommand(SoundPlayer.Descending);
	    Button.waitForAnyPress();
	    
	    
	    Engine.stop();
		SoundThread.stop();
		
		
		
		
		
		/*
		SoundPlayer SoundThread = new SoundPlayer(Thread.NORM_PRIORITY);
		Sound.pause(Sound.getTime()); //Wait until the last sound has finished
		Sound.playNote(Sound.FLUTE, 600, 500);
		Sound.pause(1000);
		LCD.drawChar('1', 1, 1);
		SoundThread.putCommand(SoundPlayer.Beep);
		LCD.drawChar('2', 2, 1);
		SoundThread.putCommand((byte) 3);
		LCD.drawChar('3', 3, 1);
		SoundThread.putCommand((byte) 4);
		LCD.drawChar('4', 4, 1);
		SoundThread.start();
		SoundThread.putCommand((byte) 3);
		LCD.drawChar('5', 5, 1);
		SoundThread.putCommand((byte) 4);
	    Button.waitForAnyPress();
	    SoundThread.putCommand((byte) 5);
	    Sound.pause(1000);
	    SoundThread.putCommand((byte) 5);
	    Sound.pause(2000);
	    SoundThread.putCommand((byte) 5);
		LCD.drawChar('6', 6, 1);
	    Button.waitForAnyPress();
	    SoundThread.stop();
	    */
		/*
		Place TestPlace;
		Transition[] TestTransition;
		Place[] TestPlaces;
		TestNet.createNet();
		TestPlace = TestNet.getStartingPlace();
		TestPlace.Activate();
		if(TestPlace.isActive()) {
			LCD.drawChar('A', 1, 1);
		} else {
			LCD.drawChar('D', 1, 1);
		}
		Button.waitForAnyPress();
		TestTransition = TestPlace.getTransitions();
		LCD.drawInt(TestTransition.length, 2, 2);
		TestTransition[0].fireIfPossible();
		TestPlaces = TestTransition[0].getPlaces_Out();
		LCD.drawInt(TestPlaces.length, 4, 2);
		Button.waitForAnyPress();
		TestTransition[0].fireIfPossible();
		Sound.beepSequenceUp();
		TestTransition[0].fireIfPossible();
		Sound.beepSequenceUp();
		TestTransition[0].fireIfPossible();
		Sound.beepSequenceUp();
		TestTransition[0].fireIfPossible();
		Sound.beepSequenceUp();
		Button.waitForAnyPress();
*/
		
/*
	    Engine.start();
	    Button.waitForAnyPress();
	    Engine.setCommand(NetStateMonitor.COMMAND_RUN);
	    
	    Sound.beepSequence();
	    Button.waitForAnyPress();
	    Engine.setCommand(NetStateMonitor.COMMAND_STOP_CYC_END);
	    Sound.beepSequence();
	    Button.waitForAnyPress();
	    Engine.stop();
	    */
	}

}
