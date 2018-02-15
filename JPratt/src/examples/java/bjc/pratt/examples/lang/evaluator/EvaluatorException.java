package bjc.pratt.examples.lang.evaluator;

/**
 * Exception thrown when evaluation goes wrong.
 * 
 * @author EVE
 *
 */
public class EvaluatorException extends RuntimeException {
	private static final long serialVersionUID = -8610585421069729811L;

	/**
	 * Create a new evaluator exception with a message and a cause.
	 * 
	 * @param message
	 *        The message for the exception.
	 * @param cause
	 *        The cause of the exception.
	 */
	public EvaluatorException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create a new evaluator exception with a message.
	 * 
	 * @param message
	 *        The message for the exception.
	 */
	public EvaluatorException(String message) {
		super(message);
	}
}