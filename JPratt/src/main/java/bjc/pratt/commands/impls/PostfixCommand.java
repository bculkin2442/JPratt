package bjc.pratt.commands.impls;

import bjc.pratt.ParserContext;
import bjc.pratt.commands.BinaryPostCommand;
import bjc.pratt.tokens.Token;
import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;

/**
 * A postfix operator.
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
public class PostfixCommand<K, V, C> extends BinaryPostCommand<K, V, C> {
	/**
	 * Create a new postfix operator.
	 *
	 * @param precedence
	 *                The precedence of the operator.
	 */
	public PostfixCommand(final int precedence) {
		super(precedence);
	}

	@Override
	public ITree<Token<K, V>> denote(final ITree<Token<K, V>> operand, final Token<K, V> operator,
			final ParserContext<K, V, C> ctx) throws ParserException {
		return new Tree<>(operator, operand);
	}
}