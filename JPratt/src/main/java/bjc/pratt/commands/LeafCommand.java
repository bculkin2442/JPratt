package bjc.pratt.commands;

import bjc.pratt.InitialCommand;
import bjc.pratt.ParserContext;
import bjc.pratt.Token;
import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;

/**
 * A operator that stands for itself.
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
public class LeafCommand<K, V, C> implements InitialCommand<K, V, C> {
	@Override
	public ITree<Token<K, V>> denote(final Token<K, V> operator, final ParserContext<K, V, C> ctx)
			throws ParserException {
		return new Tree<>(operator);
	}
}