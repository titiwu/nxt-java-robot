package piaf.tools;

/**
 * A queue that stores commands (or more general Objects)
 * that a consumer thread can retrieve.
 * @author mb
 *
 * @param <E>
 */
public class CommandQueue<E> {

	FixedSizeQueue<E> cq;

	/**
	 * 
	 * @param queue_size How long can the queue be?
	 */
	public CommandQueue(int queue_size) {
		super();
		cq = new FixedSizeQueue<E>(queue_size);
	}
	
	public synchronized E get() {
		E command;
		if (cq.empty())
			try {
				wait();
			} catch (InterruptedException e) {
				//System.out.println("InterruptedException caught");
			}
		command = cq.pop();
		notify();
		return command;
	}

	public synchronized boolean put(E command) {
		if (cq.full()) {
			return false;			
		}
		cq.push(command);
		notify();
		return true;
	}

}
