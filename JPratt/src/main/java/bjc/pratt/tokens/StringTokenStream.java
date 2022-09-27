package bjc.pratt.tokens;

import static bjc.pratt.tokens.StringToken.litToken;

import java.util.Iterator;

import bjc.data.MarkListIterator;

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
	private final MarkListIterator<Token<String, String>> iter;
	
	private Token<String, String> curr;

	/**
	 * Create a new token stream from a iterator.
	 *
	 * @param itr
	 *        The iterator to use.
	 *
	 */
	public StringTokenStream(final Iterator<Token<String, String>> itr) {
		iter = new MarkListIterator<>(itr);
	}

	@Override
	public Token<String, String> current() {
		// Prime stream if necessary
		if (curr == null)
			return next();
		return curr;
	}

	@Override
	public Token<String, String> next() {
		if(iter.hasNext()) {
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
	
	@Override
	public void mark() {
		iter.mark();
	}
	
	@Override
	public void commit() {
		iter.commit();
		
		if (!iter.hasMark()) {
			// No marks outstanding; we can release the previous state
			iter.reset();
		}
	}
	
	@Override
	public void rollback() {
		iter.rollback();
		
		curr = iter.current();
	}
	
	@Override
	public boolean hasMark() {
 		return iter.hasMark();
	}
}
