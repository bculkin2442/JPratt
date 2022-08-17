package bjc.pratt.commands.impls;

import bjc.pratt.ParserContext;
import bjc.pratt.commands.CommandResult;
import bjc.pratt.commands.NonInitialCommand;
import bjc.pratt.tokens.Token;
import bjc.data.Tree;

/**
 * Default implementation of a non-initial command.
 *
 * @author EVE
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
public class DefaultNonInitialCommand<K, V, C> extends NonInitialCommand<K, V, C> {
	@Override
	public CommandResult<K, V> denote(final Tree<Token<K, V>> operand, final Token<K, V> operator,
			final ParserContext<K, V, C> ctx) {
		throw new UnsupportedOperationException("Default command has no left denotation");
	}

	@Override
	public int leftBinding() {
		return -1;
	}
}
