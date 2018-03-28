package bjc.pratt.examples.lang.ast;

import bjc.pratt.examples.lang.evaluator.LangResult;

public class DoubleAST extends LiteralAST {
	public final double value;
	
	public DoubleAST(double vl) {
		super(LiteralType.DOUBLE);
		
		value = vl;
	}
	
	@Override
	public LangResult toResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DoubleAST other = (DoubleAST) obj;
		if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DoubleAST [value=" + value + "]";
	}
}
