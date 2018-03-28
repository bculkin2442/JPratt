package bjc.pratt.examples.lang.ast;

import bjc.pratt.examples.lang.evaluator.LangResult;

public abstract class OperatorAST extends LangAST {
	public static enum OperatorType {
		
	}
	
	public final OperatorType type;
	
	public OperatorAST(OperatorType typ) {
		super(ASTType.OPERATOR);
		
		type = typ;
	}
	
	public static OperatorAST fromToken(String tok) {
		switch (tok) {
			
		}
		
		return null;
	}
	
	@Override
	public LangResult toResult() {
		// TODO Auto-generated method stub
		return null;
	}

}
