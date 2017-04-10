package bjc.pratt.blocks;

import java.util.function.UnaryOperator;

import bjc.pratt.ParseBlock;
import bjc.pratt.ParserContext;
import bjc.pratt.Token;
import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;

/**
 * A parse block that can parse a sequnce of zero or more occurances of another
 * block.
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
public class RepeatingParseBlock<K, V, C> implements ParseBlock<K, V, C> {
	private final ParseBlock<K, V, C> innerBlock;

	private final K	delim;
	private final K	term;

	private final UnaryOperator<C> onDelim;

	private final Token<K, V> mark;

	/**
	 * Create a new repeating block.
	 *
	 * @param inner
	 *                The inner block for elements.
	 *
	 * @param delimiter
	 *                The token that delimits elements in the sequence.
	 *
	 * @param terminator
	 *                The token that terminates the sequence.
	 *
	 * @param marker
	 *                The token to use as the node in the AST.
	 *
	 * @param action
	 *                The action to apply to the state after every
	 *                delimiter.
	 */
	public RepeatingParseBlock(final ParseBlock<K, V, C> inner, final K delimiter, final K terminator,
			final Token<K, V> marker, final UnaryOperator<C> action) {
		super();

		if (inner == null)
			throw new NullPointerException("Inner block must not be null");
		else if (delimiter == null)
			throw new NullPointerException("Delimiter must not be null");
		else if (terminator == null) throw new NullPointerException("Terminator must not be null");

		innerBlock = inner;

		delim = delimiter;
		term = terminator;

		mark = marker;

		onDelim = action;
	}

	@Override
	public ITree<Token<K, V>> parse(final ParserContext<K, V, C> ctx) throws ParserException {
		final ITree<Token<K, V>> ret = new Tree<>(mark);

		Token<K, V> tok = ctx.tokens.current();

		while (!tok.getKey().equals(term)) {
			final ITree<Token<K, V>> kid = innerBlock.parse(ctx);
			ret.addChild(kid);

			tok = ctx.tokens.current();

			ctx.tokens.expect(delim, term);

			if (onDelim != null) {
				ctx.state = onDelim.apply(ctx.state);
			}
		}

		return ret;
	}

}
