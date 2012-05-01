package piaf.petrinet;

/**
 * Transition condition interface
 * @author mb
 *
 */
public interface TransitionCondition {

	/**
	 * Implements the condition for a Transition
	 * @return allowed to proceed
	 */
	public boolean condition();

}
