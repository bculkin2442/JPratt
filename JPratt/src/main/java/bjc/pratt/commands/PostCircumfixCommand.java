package bjc.pratt.commands;

import bjc.pratt.ParseBlock;
import bjc.pratt.ParserContext;
import bjc.pratt.Token;
import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;

/**
 * A post-circumfix operator, like array indexing.
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
public class PostCircumfixCommand<K, V, C> extends BinaryPostCommand<K, V, C> {
	private ParseBlock<K, V, C> innerBlock;

	private Token<K, V> mark;

	/**
	 * Create a new post-circumfix operator.
	 * 
	 * @param precedence
	 *                The precedence of the operator.
	 * 
	 * @param inner
	 *                The block inside the expression.
	 * 
	 * @param marker
	 *                The token to use as the node for the AST.
	 */
	public PostCircumfixCommand(int precedence, ParseBlock<K, V, C> inner, Token<K, V> marker) {
		super(precedence);

		if (inner == null) {
			throw new NullPointerException("Inner block must not be null");
		}

		innerBlock = inner;

		mark = marker;
	}

	@Override
	public ITree<Token<K, V>> denote(ITree<Token<K, V>> operand, Token<K, V> operator, ParserContext<K, V, C> ctx)
			throws ParserException {
		ITree<Token<K, V>> inside = innerBlock.parse(ctx);

		return new Tree<>(mark, operand, inside);
	}
}