package bjc.pratt.commands.impls;

import bjc.pratt.ParserContext;
import bjc.pratt.blocks.ParseBlock;
import bjc.pratt.commands.BinaryPostCommand;
import bjc.pratt.tokens.Token;
import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;

/**
 * A post-circumfix operator, like array indexing.
 *
 * @author bjculkin
 *
 * @param <K>
 *        The key type of the tokens.
 *
 * @param <V>
 *        The value type of the tokens.
 *
 * @param <C>
 *        The state type of the parser.
 */
public class PostCircumfixCommand<K, V, C> extends BinaryPostCommand<K, V, C> {
	private final ParseBlock<K, V, C> innerBlock;

	private final Token<K, V> mark;

	/**
	 * Create a new post-circumfix operator.
	 *
	 * @param precedence
	 *        The precedence of the operator.
	 *
	 * @param inner
	 *        The block inside the expression.
	 *
	 * @param marker
	 *        The token to use as the node for the AST.
	 */
	public PostCircumfixCommand(final int precedence, final ParseBlock<K, V, C> inner, final Token<K, V> marker) {
		super(precedence);

		if(inner == null) throw new NullPointerException("Inner block must not be null");

		innerBlock = inner;

		mark = marker;
	}

	@Override
	public ITree<Token<K, V>> denote(final ITree<Token<K, V>> operand, final Token<K, V> operator,
			final ParserContext<K, V, C> ctx) throws ParserException {
		final ITree<Token<K, V>> inside = innerBlock.parse(ctx);

		return new Tree<>(mark, operand, inside);
	}
}