package bjc.pratt.commands.impls;

import bjc.pratt.ParserContext;
import bjc.pratt.commands.AbstractInitialCommand;
import bjc.pratt.tokens.Token;
import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;

/**
 * A unary operator.
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
public class UnaryCommand<K, V, C> extends AbstractInitialCommand<K, V, C> {
	private final int nullPwer;

	/**
	 * Create a new unary command.
	 *
	 * @param precedence
	 *        The precedence of this operator.
	 */
	public UnaryCommand(final int precedence) {
		if(precedence < 0) throw new IllegalArgumentException("Precedence must be non-negative");

		nullPwer = precedence;
	}

	@Override
	protected ITree<Token<K, V>> intNullDenotation(final Token<K, V> operator, final ParserContext<K, V, C> ctx)
			throws ParserException {
		final ITree<Token<K, V>> opr = ctx.parse.parseExpression(nullPwer, ctx.tokens, ctx.state, false);

		return new Tree<>(operator, opr);
	}
}