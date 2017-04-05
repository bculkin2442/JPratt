package bjc.utils.examples.parsing;

import bjc.utils.parserutils.pratt.Token;
import bjc.utils.parserutils.pratt.tokens.StringToken;

import java.util.Set;
import java.util.function.Function;

import static bjc.utils.parserutils.pratt.tokens.StringToken.litToken;

final class Tokenizer implements Function<String, Token<String, String>> {
	private Set<String>	ops;
	private Set<String>	reserved;
	private TestContext	ctx;

	public Tokenizer(Set<String> operators, Set<String> reservedWords, TestContext context) {
		ops = operators;
		reserved = reservedWords;
		ctx = context;
	}

	@Override
	public Token<String, String> apply(String strang) {
		if (ops.contains(strang) || reserved.contains(strang)) {
			return litToken(strang);
		} else if (ctx.scopes.top().containsKey(strang)) {
			return new StringToken("(vref)", strang);
		} else {
			return new StringToken("(literal)", strang);
		}
	}
}