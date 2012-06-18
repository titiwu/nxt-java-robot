package piaf.petrinet;

/**
 * @author mb
 *
 */
public class NetStateMonitor implements PetriNetInterface {

	private byte __net_state = STATE_STOPPED;
	
	/**
	 * Accepts commands from other threads
	 * @param command
	 */
	public synchronized boolean setCommand(byte command) {
		byte OldNetState = __net_state;
		if(command == COMMAND_RUN) {
			if (__net_state == STATE_STOPPED) {
				__net_state = STATE_STARTING;
			} else if(__net_state == STATE_PAUSE) {
				__net_state = STATE_RUNNING;
			}
		} else if (command == COMMAND_RUN_1_CYC) {
			if(__net_state == STATE_STOPPED){
				__net_state = STATE_START_1_CYC;
			}
		} else if (command == COMMAND_RUN_TO_STOP) {
			if(__net_state == STATE_RUNNING){
				__net_state = STATE_RUN_TO_STOP;
			}
		} else if (command == COMMAND_STOP_NOW) {
			if(__net_state != STATE_STOPPED){
				__net_state = STATE_QUIT;
			}
		} else if (command == COMMAND_PAUSE) {
			if(__net_state == STATE_RUNNING) {
				__net_state = STATE_PAUSE;
			}
		}
		
		if(OldNetState == __net_state) {
			return false; // NetState didn't change
		} else {
			return true; // Change through the command
		}
	}
	
	/**
	 * Sets a new State (to be called only by the Net-Class itself!)
	 * @param netState
	 */
	public synchronized boolean setNetState(byte netState) {
		__net_state = netState;
		return true;
	}
	
	public byte getNetState() {
		return __net_state;
	}
	
	
}
