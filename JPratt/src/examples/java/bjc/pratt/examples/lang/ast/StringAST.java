package bjc.pratt.examples.lang.ast;

import bjc.pratt.examples.lang.evaluator.LangResult;

/**
 * Holds a string value.
 * 
 * @author student
 *
 */
public class StringAST extends LiteralAST {
	/**
	 * The value of the string.
	 */
	public final String val;

	/**
	 * Create a new string AST.
	 * 
	 * @param vl
	 *            The string value.
	 */
	public StringAST(String vl) {
		super(LiteralType.STRING);
		
		val = vl;
	}

	@Override
	public LangResult toResult() {
		// @TODO Auto-generated method stub
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((val == null) ? 0 : val.hashCode());
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
		StringAST other = (StringAST) obj;
		if (val == null) {
			if (other.val != null)
				return false;
		} else if (!val.equals(other.val))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StringAST [val=" + val + "]";
	}
}
