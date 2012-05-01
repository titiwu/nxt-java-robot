package robot.Sound;

import piaf.threads.SporadicThread;
import lejos.nxt.Sound;

/**
 * This packages plays sounds that are queued 
 * @author mb
 *
 */
public class SoundPlayer extends SporadicThread {
	
	public static final byte Beep       = 0x00;
	public static final byte DoubleBeep = 0x01;
	public static final byte Descending = 0x02;
	public static final byte Ascending  = 0x03;
	public static final byte Buzz       = 0x04;
	public static final byte StartSound = 0x05;
	
	// Melody definition: 
	// 0 is pause               Frequency, Duration, Freq., Dur.,...
	private static final int[] StartMelody = new int[]{600, 500,
                                                       0,   300,
		                                               700, 300,
		                                               800, 300,
		                                               500, 200,
		                                               400, 200};
	
	/**
	 * @param priority
	 */
	public SoundPlayer(int priority) {
		super(priority, (byte) 7);
	}


	protected void DoTask(byte NrOfSound) {
		if (NrOfSound == Beep) {
			Sound.beep();
		} else if (NrOfSound == DoubleBeep) {
			Sound.twoBeeps();
		} else if (NrOfSound == Descending) {
			Sound.beepSequence();
		} else if (NrOfSound == Ascending) {
			Sound.beepSequenceUp();
		} else if (NrOfSound == StartSound) {
			for(int index = 0; index < StartMelody.length; index += 2) {
				if (StartMelody[index] == 0) {
					Sound.pause(StartMelody[index + 1]);
				} else {
					Sound.playNote(Sound.PIANO, StartMelody[index], StartMelody[index + 1]);
				}
			}
		}
	}	
	
}
