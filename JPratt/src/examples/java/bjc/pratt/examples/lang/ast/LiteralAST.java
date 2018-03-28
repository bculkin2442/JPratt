package bjc.pratt.examples.lang.ast;

import bjc.utils.data.TopDownTransformResult;
import bjc.utils.parserutils.TokenUtils;

/**
 * AST node for a literal.
 * 
 * @author student
 *
 */
public abstract class LiteralAST extends LangAST {
	public static enum LiteralType {
		INTEGER, STRING, BOOLEAN, DOUBLE
	}
	
	public final LiteralType type;
	
	protected LiteralAST(LiteralType typ) {
		super(ASTType.LITERAL);
		
		type = typ;
	}
	/**
	 * Create a new literal AST
	 * 
	 * @param tok
	 *            The token value to build from.
	 * @return The AST for the token.
	 */
	public static LiteralAST fromToken(String tok) {
		if(tok.equalsIgnoreCase("true")) {
			return new BooleanAST(true);
		} else if(tok.equalsIgnoreCase("false")) {
			return new BooleanAST(false);
		} else if(tok.matches("[+-]?\\d+")) {
			return new IntegerAST(Integer.parseInt(tok));
		} else if(TokenUtils.isDouble(tok)) {
			return new DoubleAST(Double.parseDouble(tok));
		}
		
		return new StringAST("RAW: " + tok);
	}

	@Override
	public TopDownTransformResult getEvaluationStrategy() {
		return TopDownTransformResult.TRANSFORM;
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
		LiteralAST other = (LiteralAST) obj;
		if (type != other.type)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "LiteralAST [type=" + type + "]";
	}
}
