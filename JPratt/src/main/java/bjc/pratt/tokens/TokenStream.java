package bjc.pratt.tokens;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import bjc.utils.funcutils.StringUtils;
import bjc.utils.parserutils.ParserException;

/**
 * A stream of tokens.
 *
 * @author EVE
 *
 * @param <K>
 *        The key type of the token.
 *
 * @param <V>
 *        The value type of the token.
 */
public abstract class TokenStream<K, V> implements Iterator<Token<K, V>> {
	/**
	 * The exception thrown when an expectation fails.
	 *
	 * @author EVE
	 *
	 */
	public static class ExpectationException extends ParserException {
		private static final long serialVersionUID = 4299299480127680805L;

		/**
		 * Create a new exception with the specified message.
		 *
		 * @param msg
		 *        The message of the exception.
		 */
		public ExpectationException(final String msg) {
			super(msg);
		}
	}

	/**
	 * Get the current token.
	 *
	 * @return The current token.
	 */
	public abstract Token<K, V> current();

	@Override
	public abstract Token<K, V> next();

	@Override
	public abstract boolean hasNext();

	/**
	 * Utility method for checking that the next token is one of a specific
	 * set of types, and then consuming it.
	 *
	 * @param expectedKeys
	 *        The expected values
	 *
	 * @throws ExpectationException
	 *         If the token is not one of the expected types.
	 */
	public void expect(final Set<K> expectedKeys) throws ExpectationException {
		final K curKey = current().getKey();

		if(!expectedKeys.contains(curKey)) {
			final String expectedList = StringUtils.toEnglishList(expectedKeys.toArray(), false);

			throw new ExpectationException("One of '" + expectedList + "' was expected, not " + curKey);
		}

		next();
	}

	/**
	 * Utility method for checking that the next token is one of a specific
	 * set of types, and then consuming it.
	 *
	 * @param expectedKeys
	 *        The expected values
	 *
	 * @throws ExpectationException
	 *         If the token is not one of the expected types.
	 */
	@SafeVarargs
	public final void expect(final K... expectedKeys) throws ExpectationException {
		expect(new HashSet<>(Arrays.asList(expectedKeys)));
	}

	/**
	 * Check whether the head token is a certain type.
	 *
	 * @param val
	 *        The type to check for.
	 *
	 * @return Whether or not the head token is of that type.
	 */
	public boolean headIs(final K val) {
		return current().getKey().equals(val);
	}
}
