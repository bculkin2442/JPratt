package bjc.pratt.examples.lang.tokens;

import bjc.pratt.examples.lang.LangEvaluator.LangResult;
import bjc.pratt.tokens.Token;
import bjc.utils.data.TopDownTransformResult;

public interface LangToken {
	LangResult toResult();

	static LangToken fromToken(Token<String, String> token) {
		return null;
	}
	
	default TopDownTransformResult getEvaluationStrategy() {
		return TopDownTransformResult.PUSHDOWN;
	}
}