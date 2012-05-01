package piaf.threads;

import piaf.tools.CommandQueue;

/**
 * A sporadic thread, that runs whenever a command is sent
 * A command is a byte
 * @author mb
 *
 */
public abstract class SporadicThread extends Thread {
	
	private boolean run;
	private CommandQueue<Byte> Commands;
	
	
	public SporadicThread(int priority, byte commandQueueLength) {
		super();
		run        = true;
		this.setPriority(priority);
		Commands = new CommandQueue<Byte>(commandQueueLength);
	}
	
	public void run() {

		Byte command;
		
		while(run) {
			
			command = Commands.get();

			DoTask(command.byteValue());
		}
		
	}
	
	public void putCommand(byte command) {
		Commands.put(command);
	}
	
	public void stop() {
		run = false;
	}
	
	/**
	 * To be implemented in the derived class
	 * @param NrOfCommand
	 */
	protected abstract void DoTask(byte NrOfCommand);
}
