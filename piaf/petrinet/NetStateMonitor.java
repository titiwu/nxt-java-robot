package piaf.petrinet;

/**
 * @author mb
 *
 */
public class NetStateMonitor {
	/*
	 * Status:
	 */
	public final static byte STATE_STOPPED     = 0x00; // Stopped
	public final static byte STATE_STARTING    = 0x01; // Starting
	public final static byte STATE_START_1_CYC = 0x02; // Starting single cycle run
	public final static byte STATE_RUNNING     = 0x03; // Running
	public final static byte STATE_RUN_TO_STOP = 0x04; // Finishing cycle (Movement)
	public final static byte STATE_QUIT        = 0x05; // Stopping
	public final static byte STATE_PAUSE       = 0x06; // Paused
	public final static byte STATE_ERROR       = 0x7F; // Error
	
	private byte NetState = STATE_STOPPED;
	
	/*
	 * Commands: 
	 */
	public final static byte COMMAND_RUN         = 0x01; // Run
	public final static byte COMMAND_RUN_1_CYC   = 0x02; // Run one Cycle (Stop at cycle end)
	public final static byte COMMAND_RUN_TO_STOP = 0x03; // Stop at cycle end
	public final static byte COMMAND_STOP_NOW    = 0x04; // Stop immediately
	public final static byte COMMAND_PAUSE       = 0x05; // Take a break
	
	/**
	 * Accepts commands from other threads
	 * @param command
	 */
	public synchronized void setCommand(byte command) {
		if(command == COMMAND_RUN) {
			if (NetState == STATE_STOPPED) {
				NetState = STATE_STARTING;
			} else if(NetState == STATE_PAUSE) {
				NetState = STATE_RUNNING;
			}
		} else if (command == COMMAND_RUN_1_CYC) {
			if(NetState == STATE_STOPPED){
				NetState = STATE_START_1_CYC;
			}
		} else if (command == COMMAND_RUN_TO_STOP) {
			if(NetState == STATE_RUNNING){
				NetState = STATE_RUN_TO_STOP;
			}
		} else if (command == COMMAND_STOP_NOW) {
			if(NetState != STATE_STOPPED){
				NetState = STATE_QUIT;
			}
		} else if (command == COMMAND_PAUSE) {
			if(NetState == STATE_RUNNING) {
				NetState = STATE_PAUSE;
			}
		}
	}
	
	/**
	 * Sets a new State (to be called only by the Net-Class itself!)
	 * @param netState
	 */
	public synchronized void setNetState(byte netState) {
		NetState = netState;
	}
	
	public byte getNetState() {
		return NetState;
	}
	
	
}
