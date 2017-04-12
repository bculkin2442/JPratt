package bjc.pratt.blocks;

import java.util.function.Predicate;

import bjc.pratt.ParserContext;
import bjc.pratt.tokens.Token;
import bjc.utils.data.ITree;
import bjc.utils.parserutils.ParserException;

/**
 * Simple implementation of {@link ParseBlock}
 *
 * @author bjculkin
 *
 * @param <K>
 *                The key type of the tokens.
 *
 * @param <V>
 *                The value type of the tokens.
 *
 * @param <C>
 *                The state type of the parser.
 */
public class SimpleParseBlock<K, V, C> implements ParseBlock<K, V, C> {
	private final int pow;

	private final K term;

	private final Predicate<ITree<Token<K, V>>> validatr;

	/**
	 * Create a new block.
	 *
	 * @param precedence
	 *                The precedence of this block.
	 * @param validator
	 *                The predicate to apply to blocks.
	 * @param terminator
	 *                The token type that terminates the block. If this is
	 *                null, don't check for a terminator.
	 */
	public SimpleParseBlock(final int precedence, final Predicate<ITree<Token<K, V>>> validator,
			final K terminator) {
		if (precedence < 0) throw new IllegalArgumentException("Precedence must be non-negative");

		pow = precedence;
		term = terminator;
		validatr = validator;
	}

	@Override
	public ITree<Token<K, V>> parse(final ParserContext<K, V, C> ctx) throws ParserException {
		final ITree<Token<K, V>> res = ctx.parse.parseExpression(pow, ctx.tokens, ctx.state, false);

		if (term != null) {
			ctx.tokens.expect(term);
		}

		if (validatr == null || validatr.test(res)) return res;

		throw new ParserException("Block failed validation");
	}

	@Override
	public int hashCode() {
		final int prime = 31;

		int result = 1;

		result = prime * result + pow;
		result = prime * result + (term == null ? 0 : term.hashCode());

		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof SimpleParseBlock)) return false;

		final SimpleParseBlock<?, ?, ?> other = (SimpleParseBlock<?, ?, ?>) obj;

		if (pow != other.pow) return false;

		if (term == null) {
			if (other.term != null) return false;
		} else if (!term.equals(other.term)) return false;

		return true;
	}
}