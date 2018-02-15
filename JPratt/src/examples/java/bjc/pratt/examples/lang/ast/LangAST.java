package bjc.pratt.examples.lang.ast;

import bjc.pratt.examples.lang.evaluator.LangResult;
import bjc.pratt.tokens.Token;
import bjc.utils.data.TopDownTransformResult;

public interface LangAST {
	LangResult toResult();

	static LangAST fromToken(Token<String, String> token) {
		return null;
	}
	
	default TopDownTransformResult getEvaluationStrategy() {
		return TopDownTransformResult.PUSHDOWN;
	}
}