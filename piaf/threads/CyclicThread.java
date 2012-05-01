package piaf.threads;

/**
 * A cyclic thread
 * @author mb
 *
 */
public abstract class CyclicThread extends Thread {

	private int CycleTime;
	private boolean run;
	
	/**
	 * Constructor
	 * @param priority   1-10
	 * @param cycle_Time in ms
	 */
	public CyclicThread(int priority, int cycleTime) {
		super();
		run        = true;
		CycleTime = cycleTime;
		this.setPriority(priority);
	}
	
	public void run() {
		int sleep_Duration;
		
		int next_Time = (int)System.currentTimeMillis();
		
		while(run) {
		    sleep_Duration = (next_Time - (int)System.currentTimeMillis());
		    if (sleep_Duration > 0) {
				try {
				    sleep(sleep_Duration);
				} catch (Exception e) {
					System.out.println ("Err - " + e );
				}
		    }
			// Do the Cyclic work
			cyclicTask();
			// Next time the task should be executed 
			next_Time       += CycleTime;
		}
		
	}
	
	public void stop() {
		run = false;
	}
	
	protected abstract void cyclicTask();

}
