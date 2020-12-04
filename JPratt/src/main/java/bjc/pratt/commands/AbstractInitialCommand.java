package bjc.pratt.commands;

import bjc.pratt.ParserContext;
import bjc.pratt.tokens.Token;
import bjc.data.Tree;
import bjc.utils.parserutils.ParserException;

/**
 * Abstract base for initial commands.
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
public abstract class AbstractInitialCommand<K, V, C> implements InitialCommand<K, V, C> {
	@Override
	public Tree<Token<K, V>> denote(final Token<K, V> operator,
			final ParserContext<K, V, C> ctx) throws ParserException {
		return intNullDenotation(operator, ctx);
	}

	/**
	 * Internal null denotation method.
	 * 
	 * @param operator
	 *                 The operator that was parsed.
	 * @param ctx
	 *                 The parser context at this point.
	 * 
	 * @return The tree that this method parsed.
	 * 
	 * @throws ParserException
	 *                         If something went wrong while parsing.
	 */
	protected abstract Tree<Token<K, V>> intNullDenotation(Token<K, V> operator,
			ParserContext<K, V, C> ctx) throws ParserException;

}