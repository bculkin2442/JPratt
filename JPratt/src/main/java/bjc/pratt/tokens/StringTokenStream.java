package bjc.pratt.tokens;

import static bjc.pratt.tokens.StringToken.litToken;

import java.util.Iterator;

/**
 * Simple implementation of token stream for strings.
 *
 * The terminal token here is represented by a token with type and value
 * '(end)'.
 *
 * @author EVE
 *
 */
public class StringTokenStream extends TokenStream<String, String> {
	private final Iterator<Token<String, String>> iter;

	private Token<String, String> curr;

	/**
	 * Create a new token stream from a iterator.
	 *
	 * @param itr
	 *                The iterator to use.
	 *
	 */
	public StringTokenStream(final Iterator<Token<String, String>> itr) {
		iter = itr;

	}

	@Override
	public Token<String, String> current() {
		return curr;
	}

	@Override
	public Token<String, String> next() {
		if (iter.hasNext()) {
			curr = iter.next();
		} else {
			curr = litToken("(end)");
		}

		return curr;
	}

	@Override
	public boolean hasNext() {
		return iter.hasNext();
	}
}
