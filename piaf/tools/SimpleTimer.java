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
	
	private int WaitTime  = 0;
	private int StartTime = 0;
	private boolean isStarted = false;
	
	/**
	 * Default contructor
	 */
	public SimpleTimer() {
	}
	
	/**
	 * Sets the waiting time
	 * @param waitTime (in ms)
	 */
	public SimpleTimer(int waitTime) {
		WaitTime = waitTime;
	}
	
	/**
	 * Sets the waiting time
	 * @param waitTime (in ms)
	 */
	public void setWaitTime(int waitTime) {
		WaitTime = waitTime;
	}
	
	/**
	 * Starts the timer (sets the start time)
	 */
	public void startTimer() {
		StartTime = (int)System.currentTimeMillis();
		isStarted = true;
	}
	
	/**
	 * True, if the time has passed
	 * @return has the time passed?
	 */
	public boolean timedOut() {
		if ((int)System.currentTimeMillis() - StartTime >= WaitTime) {
			isStarted = false;
			return true;
		} else {
			return false;	
		}		
	}
	
	/**
	 * Returns true if the timer has been started
	 * @return isStarted
	 */
	public boolean isStarted() {
		return isStarted;
	}
	
	/**
	 * Gives the remaining ms
	 * @return remaining time
	 */
	public int timeLeft() {
		return (WaitTime - (int)System.currentTimeMillis() + StartTime);
	}
	
}
