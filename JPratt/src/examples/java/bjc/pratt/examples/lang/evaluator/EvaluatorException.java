package bjc.pratt.examples.lang.evaluator;

public class EvaluatorException extends RuntimeException {
	private static final long serialVersionUID = -8610585421069729811L;

	public EvaluatorException(String message, Throwable cause) {
		super(message, cause);
	}

	public EvaluatorException(String message) {
		super(message);
	}
}