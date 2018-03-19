package bjc.pratt.examples.lang.ast;

import bjc.pratt.examples.lang.evaluator.LangResult;

/**
 * AST containing an integer.
 * 
 * @author student
 *
 */
public class IntegerAST extends LiteralAST {
	/**
	 * Value of the integer.
	 */
	public final int val;

	/**
	 * Create a new integer AST.
	 * 
	 * @param vl
	 *            The value of the integer.
	 */
	public IntegerAST(int vl) {
		super(LiteralType.INTEGER);
		
		val = vl;
	}

	@Override
	public LangResult toResult() {
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + val;
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
		IntegerAST other = (IntegerAST) obj;
		if (val != other.val)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "IntegerAST [val=" + val + "]";
	}
}
