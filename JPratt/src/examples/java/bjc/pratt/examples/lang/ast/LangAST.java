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
public abstract class LangAST {
	public static enum ASTType {
		LITERAL, OPERATOR
	}
	
	public final ASTType type;
	
	protected LangAST(ASTType typ) {
		type = typ;
	}
	
	/**
	 * Evaluate the AST.
	 * 
	 * @return The evaluated AST
	 */
	public abstract LangResult toResult();

	/**
	 * Create an AST from a token.
	 * 
	 * @param token
	 *            The token to create the AST from
	 * @return The new AST
	 * @throws EvaluatorException
	 *             If something goes wrong.
	 */
	public static LangAST fromToken(Token<String, String> token) throws EvaluatorException {
		String key = token.getKey();

		switch (key) {
		case "(literal)":
			return LiteralAST.fromToken(token.getValue());
		default:
			String msg = String.format("Unknown token type '%s'", key);

			// @TODO uncomment this later
			//throw new EvaluatorException(msg);
			return new StringAST("RAW: " + token.toString());
		}
	}

	/**
	 * Get the way an AST node should be evaluated.
	 * 
	 * @return The way to evaluate the AST node.
	 */
	public TopDownTransformResult getEvaluationStrategy() {
		return TopDownTransformResult.PUSHDOWN;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LangAST other = (LangAST) obj;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LangAST [type=" + type + "]";
	}
}