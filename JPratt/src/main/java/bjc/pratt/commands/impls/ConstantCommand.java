package bjc.pratt.commands.impls;

import bjc.pratt.ParserContext;
import bjc.pratt.commands.CommandResult;
import bjc.pratt.commands.InitialCommand;
import bjc.pratt.tokens.Token;
import bjc.data.Tree;
import bjc.utils.parserutils.ParserException;

/**
 * A command that represents a specific tree.
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
public class ConstantCommand<K, V, C> implements InitialCommand<K, V, C> {
	private final Tree<Token<K, V>> val;

	/**
	 * Create a new constant.
	 *
	 * @param con
	 *        The tree this constant represents.
	 */
	public ConstantCommand(final Tree<Token<K, V>> con) {
		val = con;
	}

	@Override
	public CommandResult<K, V> denote(final Token<K, V> operator, final ParserContext<K, V, C> ctx)
			throws ParserException {
		return CommandResult.success(val);
	}
}