package bjc.pratt.commands.impls;

import java.util.Set;

import bjc.pratt.ParserContext;
import bjc.pratt.commands.BinaryPostCommand;
import bjc.pratt.tokens.Token;
import bjc.data.Tree;
import bjc.data.SimpleTree;
import bjc.utils.parserutils.ParserException;

/**
 * Create a new chained operator.
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
public class ChainCommand<K, V, C> extends BinaryPostCommand<K, V, C> {
	private final Set<K> chainWith;

	private final Token<K, V> chain;

	/**
	 * Create a new chained operator.
	 *
	 * @param precedence
	 *        The precedence of this operator.
	 *
	 * @param chainSet
	 *        The operators to chain with.
	 *
	 * @param chainMarker
	 *        The token to use as the node in the AST.
	 */
	public ChainCommand(final int precedence, final Set<K> chainSet, final Token<K, V> chainMarker) {
		super(precedence);

		chainWith = chainSet;
		chain = chainMarker;
	}

	@Override
	public Tree<Token<K, V>> denote(final Tree<Token<K, V>> operand, final Token<K, V> operator,
			final ParserContext<K, V, C> ctx) throws ParserException {
		final Tree<Token<K, V>> tree = ctx.parse.parseExpression(1 + leftBinding(), ctx.tokens, ctx.state,
				false);

		final Tree<Token<K, V>> res = new SimpleTree<>(operator, operand, tree);

		if(chainWith.contains(ctx.tokens.current().getKey())) {
			final Token<K, V> tok = ctx.tokens.current();
			ctx.tokens.next();

			final Tree<Token<K, V>> other = denote(tree, tok,
					new ParserContext<>(ctx.tokens, ctx.parse, ctx.state));

			return new SimpleTree<>(chain, res, other);
		}

		return res;
	}

	@Override
	public int nextBinding() {
		return leftBinding() - 1;
	}
}