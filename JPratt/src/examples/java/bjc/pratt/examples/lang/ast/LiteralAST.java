package bjc.pratt.examples.lang.ast;

import bjc.pratt.tokens.Token;
import bjc.utils.data.TopDownTransformResult;

/**
 * AST node for a literal.
 * 
 * @author student
 *
 */
public interface LiteralAST extends LangAST {
	/**
	 * Create a new literal AST
	 * 
	 * @param tok
	 *            The token to build from.
	 * @return The AST for the token.
	 */
	static LiteralAST fromToken(Token<String, String> tok) {
		return null;
	}

	@Override
	default TopDownTransformResult getEvaluationStrategy() {
		return TopDownTransformResult.TRANSFORM;
	}
}
