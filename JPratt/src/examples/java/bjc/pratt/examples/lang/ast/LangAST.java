package bjc.pratt.examples.lang.ast;

import bjc.pratt.examples.lang.evaluator.EvaluatorException;
import bjc.pratt.examples.lang.evaluator.LangResult;
import bjc.pratt.tokens.Token;
import bjc.utils.data.TopDownTransformResult;

public interface LangAST {
	LangResult toResult();

	static LangAST fromToken(Token<String, String> token) throws EvaluatorException {
		String key = token.getKey();

		switch(key) {
		case "(literal)":
			return LiteralAST.fromToken(token);
		default:
			String msg = String.format("Unknown token type '%s'", key);

			throw new EvaluatorException(msg);
		}
	}

	default TopDownTransformResult getEvaluationStrategy() {
		return TopDownTransformResult.PUSHDOWN;
	}
}