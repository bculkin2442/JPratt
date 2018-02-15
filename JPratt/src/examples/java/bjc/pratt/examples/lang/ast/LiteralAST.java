package bjc.pratt.examples.lang.ast;

import bjc.pratt.tokens.Token;
import bjc.utils.data.TopDownTransformResult;

public interface LiteralAST extends LangAST {
	static LiteralAST fromToken(Token<String, String> tok) {
		return null;
	}

	@Override
	default TopDownTransformResult getEvaluationStrategy() {
		return TopDownTransformResult.TRANSFORM;
	}
}
