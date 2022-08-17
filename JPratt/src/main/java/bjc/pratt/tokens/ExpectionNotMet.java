package bjc.pratt.tokens;

import bjc.utils.parserutils.ParserException;

/**
 * The exception thrown when an expectation fails.
 *
 * @author EVE
 *
 */
public class ExpectionNotMet extends ParserException {
	private static final long serialVersionUID = 4299299480127680805L;

	/**
	 * Create a new exception with the specified message.
	 *
	 * @param msg
	 *        The message of the exception.
	 */
	public ExpectionNotMet(final String msg) {
		super(msg);
	}
}