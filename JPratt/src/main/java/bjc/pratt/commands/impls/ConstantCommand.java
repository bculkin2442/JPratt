package bjc.pratt.commands.impls;

import bjc.pratt.ParserContext;
import bjc.pratt.commands.InitialCommand;
import bjc.pratt.tokens.Token;
import bjc.utils.data.ITree;
import bjc.utils.parserutils.ParserException;

/**
 * A command that represents a specific tree.
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
public class ConstantCommand<K, V, C> implements InitialCommand<K, V, C> {
	private final ITree<Token<K, V>> val;

	/**
	 * Create a new constant.
	 *
	 * @param con
	 *                The tree this constant represents.
	 */
	public ConstantCommand(final ITree<Token<K, V>> con) {
		val = con;
	}

	@Override
	public ITree<Token<K, V>> denote(final Token<K, V> operator, final ParserContext<K, V, C> ctx)
			throws ParserException {
		return val;
	}
}