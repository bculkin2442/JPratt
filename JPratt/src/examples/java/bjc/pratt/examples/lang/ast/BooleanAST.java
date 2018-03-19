package bjc.pratt.examples.lang.ast;

import bjc.pratt.examples.lang.evaluator.LangResult;

public class BooleanAST extends LiteralAST {
	public final boolean val;
	
	public BooleanAST(boolean vl) {
		super(LiteralType.BOOLEAN);
		
		val = vl;
	}
	
	@Override
	public LangResult toResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (val ? 1231 : 1237);
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
		BooleanAST other = (BooleanAST) obj;
		if (val != other.val)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BooleanAST [val=" + val + "]";
	}
}
