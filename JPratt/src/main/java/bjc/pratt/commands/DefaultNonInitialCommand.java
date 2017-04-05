package bjc.pratt.commands;

import bjc.pratt.NonInitialCommand;
import bjc.pratt.ParserContext;
import bjc.pratt.Token;
import bjc.utils.data.ITree;

/**
 * Default implementation of a non-initial command.
 * 
 * @author EVE
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
public class DefaultNonInitialCommand<K, V, C> extends NonInitialCommand<K, V, C> {
	@Override
	public ITree<Token<K, V>> denote(ITree<Token<K, V>> operand, Token<K, V> operator, ParserContext<K, V, C> ctx) {
		throw new UnsupportedOperationException("Default command has no left denotation");
	}

	@Override
	public int leftBinding() {
		return -1;
	}
}
