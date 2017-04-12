package bjc.pratt.commands.impls;

import bjc.pratt.ParserContext;
import bjc.pratt.blocks.ParseBlock;
import bjc.pratt.commands.AbstractInitialCommand;
import bjc.pratt.tokens.Token;
import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;

/**
 * A grouping operator.
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
public class GroupingCommand<K, V, C> extends AbstractInitialCommand<K, V, C> {
	private final ParseBlock<K, V, C> innerBlock;

	private final Token<K, V> mark;

	/**
	 * Create a new grouping command.
	 *
	 * @param inner
	 *                The inner block.
	 *
	 * @param marker
	 *                The token to use as the node in the AST.
	 */
	public GroupingCommand(final ParseBlock<K, V, C> inner, final Token<K, V> marker) {
		innerBlock = inner;

		mark = marker;
	}

	@Override
	protected ITree<Token<K, V>> intNullDenotation(final Token<K, V> operator, final ParserContext<K, V, C> ctx)
			throws ParserException {
		final ITree<Token<K, V>> opr = innerBlock.parse(ctx);

		return new Tree<>(mark, opr);
	}
}