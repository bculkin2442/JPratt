package bjc.pratt.tokens;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import bjc.utils.funcutils.StringUtils;

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
	 * Place a mark in the current stream, which can be either returned to or abandoned later on.
	 */
	public abstract void mark();
	
	/**
	 * Reset the stream to the state it was in when the last mark was taken.
	 */
	public abstract void rollback();
	
	/**
	 * Check if the stream has at least one mark.
	 * 
	 * @return Whether or not at least one mark exists.
	 */
	public abstract boolean hasMark();
	
	/**
	 * Remove the last mark placed into the stream. This prevents returning to it later on.
	 */
	public abstract void commit();
	
	/**
	 * Utility method for checking that the next token is one of a specific
	 * set of types, and then consuming it.
	 *
	 * @param expectedKeys
	 *        The expected values
	 *
	 * @throws ExpectionNotMet
	 *         If the token is not one of the expected types.
	 */
	public void expect(final Set<K> expectedKeys) throws ExpectionNotMet {
		final K curKey = current().getKey();

		if(!expectedKeys.contains(curKey)) {
			final String expectedList = StringUtils.toEnglishList(expectedKeys.toArray(), false);

			throw new ExpectionNotMet("One of '" + expectedList + "' was expected, not " + curKey);
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
	 * @throws ExpectionNotMet
	 *         If the token is not one of the expected types.
	 */
	@SafeVarargs
	public final void expect(final K... expectedKeys) throws ExpectionNotMet {
		HashSet<K> keys = new HashSet<>(Arrays.asList(expectedKeys));
		expect(keys);
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
