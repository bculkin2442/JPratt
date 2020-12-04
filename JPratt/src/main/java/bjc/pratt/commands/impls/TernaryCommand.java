package bjc.pratt.commands.impls;

import bjc.pratt.ParserContext;
import bjc.pratt.blocks.ParseBlock;
import bjc.pratt.commands.BinaryPostCommand;
import bjc.pratt.tokens.Token;
import bjc.data.Tree;
import bjc.data.SimpleTree;
import bjc.utils.parserutils.ParserException;

/**
 * A ternary command, like C's ?:
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
public class TernaryCommand<K, V, C> extends BinaryPostCommand<K, V, C> {
	private final ParseBlock<K, V, C> innerBlck;

	private final Token<K, V> mark;

	private final boolean nonassoc;

	/**
	 * Create a new ternary command.
	 *
	 * @param precedence
	 *        The precedence of this operator.
	 *
	 * @param innerBlock
	 *        The representation of the inner block of the expression.
	 *
	 * @param marker
	 *        The token to use as the root of the AST node.
	 *
	 * @param isNonassoc
	 *        Whether or not the conditional is associative.
	 */
	public TernaryCommand(final int precedence, final ParseBlock<K, V, C> innerBlock, final Token<K, V> marker,
			final boolean isNonassoc) {
		super(precedence);

		if(innerBlock == null)
			throw new NullPointerException("Inner block must not be null");
		else if(marker == null) throw new NullPointerException("Marker must not be null");

		innerBlck = innerBlock;
		mark = marker;
		nonassoc = isNonassoc;
	}

	@Override
	public Tree<Token<K, V>> denote(final Tree<Token<K, V>> operand, final Token<K, V> operator,
			final ParserContext<K, V, C> ctx) throws ParserException {
		final Tree<Token<K, V>> inner = innerBlck.parse(ctx);

		final Tree<Token<K, V>> outer = ctx.parse.parseExpression(1 + leftBinding(), ctx.tokens, ctx.state,
				false);

		return new SimpleTree<>(mark, inner, operand, outer);
	}

	@Override
	public int nextBinding() {
		if(nonassoc) return leftBinding() - 1;

		return leftBinding();
	}
}