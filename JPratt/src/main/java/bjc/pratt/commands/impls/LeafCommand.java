package bjc.pratt.commands.impls;

import bjc.pratt.ParserContext;
import bjc.pratt.commands.InitialCommand;
import bjc.pratt.tokens.Token;
import bjc.data.Tree;
import bjc.data.SimpleTree;
import bjc.utils.parserutils.ParserException;

/**
 * A operator that stands for itself.
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
public class LeafCommand<K, V, C> implements InitialCommand<K, V, C> {
	@Override
	public Tree<Token<K, V>> denote(final Token<K, V> operator, final ParserContext<K, V, C> ctx)
			throws ParserException {
		return new SimpleTree<>(operator);
	}
}