package bjc.pratt.commands;

import bjc.pratt.ParseBlock;
import bjc.pratt.ParserContext;
import bjc.pratt.Token;
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
	private ParseBlock<K, V, C> innerBlock;

	private Token<K, V> mark;

	/**
	 * Create a new grouping command.
	 * 
	 * @param inner
	 *                The inner block.
	 * 
	 * @param marker
	 *                The token to use as the node in the AST.
	 */
	public GroupingCommand(ParseBlock<K, V, C> inner, Token<K, V> marker) {
		innerBlock = inner;

		mark = marker;
	}

	@Override
	protected ITree<Token<K, V>> intNullDenotation(Token<K, V> operator, ParserContext<K, V, C> ctx)
			throws ParserException {
		ITree<Token<K, V>> opr = innerBlock.parse(ctx);

		return new Tree<>(mark, opr);
	}
}