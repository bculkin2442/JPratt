package bjc.pratt.commands;

import bjc.pratt.ParserContext;
import bjc.pratt.commands.CommandResult.Status;
import bjc.pratt.tokens.Token;
import bjc.data.Tree;
import bjc.data.SimpleTree;
import bjc.utils.parserutils.ParserException;

/**
 * A binary operator.
 *
 * @author bjculkin
 *
 * @param <K>
 *            The key type of the tokens.
 *
 * @param <V>
 *            The value type of the tokens.
 *
 * @param <C>
 *            The state type of the parser.
 */
public abstract class BinaryCommand<K, V, C> extends BinaryPostCommand<K, V, C> {
	/**
	 * Create a new binary operator with the specified precedence.
	 *
	 * @param precedence
	 *                   The precedence of the operator.
	 */
	public BinaryCommand(final int precedence) {
		super(precedence);
	}

	/**
	 * The right-binding power (right-precedence) of this command.
	 * 
	 * @return The right binding power of this command.
	 */
	protected abstract int rightBinding();

	@Override
	public CommandResult<K, V> denote(final Tree<Token<K, V>> operand,
			final Token<K, V> operator, final ParserContext<K, V, C> ctx)
			throws ParserException {
		final CommandResult<K,V> opr
				= ctx.parse.parseExpression(rightBinding(), ctx.tokens, ctx.state, false);
		
		if (opr.status != Status.SUCCESS) return opr;

		return CommandResult.success(new SimpleTree<>(operator, operand, opr.success()));
	}
}