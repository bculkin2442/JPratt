package bjc.pratt.commands.impls;

import bjc.pratt.ParserContext;
import bjc.pratt.commands.CommandResult;
import bjc.pratt.commands.InitialCommand;
import bjc.pratt.tokens.Token;
import bjc.data.Tree;
import bjc.utils.parserutils.ParserException;

/**
 * Default implementation of an initial command.
 *
 * @author EVE
 *
 * @param <K>
 *        The key type of the token.
 *
 * @param <V>
 *        The value type of the token.
 *
 * @param <C>
 *        The state type of the parser.
 */
public class DefaultInitialCommand<K, V, C> implements InitialCommand<K, V, C> {
	@Override
	public CommandResult<K, V> denote(final Token<K, V> operator, final ParserContext<K, V, C> ctx)
			throws ParserException {
		throw new ParserException("Unexpected token " + operator);
	}
}
