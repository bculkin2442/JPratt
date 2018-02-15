package bjc.pratt.examples.lang;

import static bjc.pratt.tokens.StringToken.litToken;

import java.util.Set;
import java.util.function.Function;

import bjc.pratt.tokens.StringToken;
import bjc.pratt.tokens.Token;

final class Tokenizer implements Function<String, Token<String, String>> {
	private final Set<String>	ops;
	private final Set<String>	reserved;

	@SuppressWarnings("unused")
	private final TestContext ctx;

	public Tokenizer(final Set<String> operators, final Set<String> reservedWords, final TestContext context) {
		ops = operators;
		reserved = reservedWords;
		ctx = context;
	}

	@Override
	public Token<String, String> apply(final String strang) {
		if(ops.contains(strang) || reserved.contains(strang))
			return litToken(strang);
		else if(strang.matches("(?:[\\u00B2\\u00B3\\u00B9\\u2070]|[\\u2074-\\u2079])+")) {
			/*
			 * This regular expression matches series of unicode
			 * super - scripts 1 - 9.
			 */
			String subscript = strang;

			return new StringToken("(superexp)", subscript);
		} else {
			return new StringToken("(literal)", strang);
		}
	}
}
