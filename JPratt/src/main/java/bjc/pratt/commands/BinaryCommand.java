package bjc.pratt.commands;

import bjc.pratt.ParserContext;
import bjc.pratt.Token;
import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;

/**
 * A binary operator.
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
public abstract class BinaryCommand<K, V, C> extends BinaryPostCommand<K, V, C> {
	/**
	 * Create a new binary operator with the specified precedence.
	 * 
	 * @param precedence
	 *                The precedence of the operator.
	 */
	public BinaryCommand(int precedence) {
		super(precedence);
	}

	protected abstract int rightBinding();

	@Override
	public ITree<Token<K, V>> denote(ITree<Token<K, V>> operand, Token<K, V> operator, ParserContext<K, V, C> ctx)
			throws ParserException {
		ITree<Token<K, V>> opr = ctx.parse.parseExpression(rightBinding(), ctx.tokens, ctx.state, false);

		return new Tree<>(operator, operand, opr);
	}
}