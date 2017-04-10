package bjc.pratt.commands;

import bjc.pratt.InitialCommand;
import bjc.pratt.ParserContext;
import bjc.pratt.Token;
import bjc.utils.data.ITree;
import bjc.utils.parserutils.ParserException;

/**
 * Default implementation of an initial command.
 *
 * @author EVE
 *
 * @param <K>
 *                The key type of the token.
 *
 * @param <V>
 *                The value type of the token.
 *
 * @param <C>
 *                The state type of the parser.
 */
public class DefaultInitialCommand<K, V, C> implements InitialCommand<K, V, C> {
	@Override
	public ITree<Token<K, V>> denote(final Token<K, V> operator, final ParserContext<K, V, C> ctx)
			throws ParserException {
		throw new ParserException("Unexpected token " + operator);
	}
}
