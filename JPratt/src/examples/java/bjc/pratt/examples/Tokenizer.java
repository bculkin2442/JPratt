package bjc.pratt.examples;

import bjc.pratt.Token;
import bjc.pratt.tokens.StringToken;

import java.util.Set;
import java.util.function.Function;

import static bjc.pratt.tokens.StringToken.litToken;

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
		} else if(strang.matches("(?:[\\u00B2\\u00B3\\u00B9\\u2070]|[\\u2074-\\u2079])+")) {
			/*
			 * This regular expression matches series of unicode super-scripts 1-9.
			 */
			return new StringToken("(superexp)", strang);
		} else {
			return new StringToken("(literal)", strang);
		}
	}
}
