package piaf.petrinet;

/**
 * Every place needs attached actions with this interface
 * @author mb
 *
 */
public interface PlaceActions {
	/**
	 * Action executed on entry
	 */
	public abstract void actionOnEntry();
	/**
	 * Action executed every cycle while active
	 */
	public abstract void actionWhileActive();
	/**
	 * Action when exiting the place
	 */
	public abstract void actionExit();
	/**
	 * Stop everything that has been started
	 */
	public abstract void stopAction();
}
