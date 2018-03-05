package bjc.pratt.examples.lang.ast;

import bjc.pratt.examples.lang.evaluator.EvaluatorException;
import bjc.pratt.examples.lang.evaluator.LangResult;
import bjc.pratt.tokens.Token;
import bjc.utils.data.TopDownTransformResult;

/**
 * Represents the AST for the language.
 * 
 * @author student
 *
 */
public interface LangAST {
	/**
	 * Evaluate the AST.
	 * 
	 * @return The evaluated AST
	 */
	LangResult toResult();

	/**
	 * Create an AST from a token.
	 * 
	 * @param token
	 *            The token to create the AST from
	 * @return The new AST
	 * @throws EvaluatorException
	 *             If something goes wrong.
	 */
	static LangAST fromToken(Token<String, String> token) throws EvaluatorException {
		String key = token.getKey();

		switch (key) {
		case "(literal)":
			return LiteralAST.fromToken(token);
		default:
			String msg = String.format("Unknown token type '%s'", key);

			throw new EvaluatorException(msg);
		}
	}

	/**
	 * Get the way an AST node should be evaluated.
	 * 
	 * @return The way to evaluate the AST node.
	 */
	default TopDownTransformResult getEvaluationStrategy() {
		return TopDownTransformResult.PUSHDOWN;
	}
}