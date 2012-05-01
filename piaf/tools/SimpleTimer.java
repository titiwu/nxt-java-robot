package piaf.tools;

/**
 * A very simplistic timer that has to be polled
 * Usage:
 * MyTimer.setWaitTime(yourWaitingTimeInMs);
 * MyTimer.startTimer();
 * if(MyTimer.timedOut()) {
 * 	// Time has passed!
 * }
 * @author mb
 *
 */
public class SimpleTimer {
	
	private int WaitTime = 0;
	private int StartTime = 0;
	
	/**
	 * Sets the waiting time
	 * @param waitTime
	 */
	public void setWaitTime(int waitTime) {
		WaitTime = waitTime;
	}
	
	/**
	 * Starts the timer (sets the start time)
	 */
	public void startTimer() {
		StartTime = (int)System.currentTimeMillis();
	}
	
	/**
	 * True, if the time has passed
	 * @return has the time passed?
	 */
	public boolean timedOut() {
		return ((int)System.currentTimeMillis() - StartTime >= WaitTime);
	}
	
	/**
	 * Gives the remaining ms
	 * @return remaining time
	 */
	public int timeLeft() {
		return (WaitTime - (int)System.currentTimeMillis() + StartTime);
	}
	
}
