package bjc.pratt.examples.lang;

import bjc.pratt.examples.lang.tokens.LangToken;
import bjc.utils.data.ITree;

public class LangEvaluator {
	public static class EvaluatorException extends RuntimeException {
		private static final long serialVersionUID = -8610585421069729811L;

		public EvaluatorException(String message, Throwable cause) {
			super(message, cause);
		}

		public EvaluatorException(String message) {
			super(message);
		}
	}

	public static class LangResult {

	}

	public LangResult evaluate(ITree<LangToken> ast) {
		ITree<LangToken> evaluatedTree = ast.topDownTransform(LangToken::getEvaluationStrategy, this::evaluateNode);

		return evaluatedTree.getHead().toResult();
	}

	private ITree<LangToken> evaluateNode(ITree<LangToken> node) {
		return node;
	}
}