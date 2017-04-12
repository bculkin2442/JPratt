package bjc.pratt.blocks;

import java.util.function.Function;

import bjc.pratt.ParseBlock;
import bjc.pratt.ParserContext;
import bjc.pratt.PrattParser;
import bjc.pratt.Token;
import bjc.pratt.TokenStream;
import bjc.utils.data.ITree;
import bjc.utils.funcutils.Isomorphism;
import bjc.utils.parserutils.ParserException;

/**
 * A {@link ParseBlock} that parses an expression from a 'inner' grammar.
 *
 * @author bjculkin
 *
 * @param <K>
 *                The key type of the outer tokens.
 *
 * @param <V>
 *                The value type of the outer tokens.
 *
 * @param <C>
 *                The state type of the outer parser.
 *
 * @param <K2>
 *                The key type of the inner tokens.
 *
 * @param <V2>
 *                The value type of the inner tokens.
 *
 * @param <C2>
 *                The state type of the outer parser.
 */
public class GrammarParseBlock<K, V, C, K2, V2, C2> implements ParseBlock<K, V, C> {
	private final PrattParser<K2, V2, C2> innr;

	private final int	prcedence;
	private final boolean	isStatemnt;

	private final Function<TokenStream<K, V>, TokenStream<K2, V2>>		tkenTransform;
	private final Isomorphism<C, C2>					stteTransform;
	private final Function<ITree<Token<K2, V2>>, ITree<Token<K, V>>>	xpressionTransform;

	/**
	 * Create a new grammar parser block.
	 *
	 * @param inner
	 * @param precedence
	 * @param isStatement
	 * @param tokenTransform
	 * @param stateTransform
	 * @param expressionTransform
	 */
	public GrammarParseBlock(final PrattParser<K2, V2, C2> inner, final int precedence, final boolean isStatement,
			final Function<TokenStream<K, V>, TokenStream<K2, V2>> tokenTransform,
			final Isomorphism<C, C2> stateTransform,
			final Function<ITree<Token<K2, V2>>, ITree<Token<K, V>>> expressionTransform) {
		innr = inner;
		prcedence = precedence;
		isStatemnt = isStatement;
		tkenTransform = tokenTransform;
		stteTransform = stateTransform;
		xpressionTransform = expressionTransform;
	}

	@Override
	public ITree<Token<K, V>> parse(final ParserContext<K, V, C> ctx) throws ParserException {
		final C2 newState = stteTransform.to(ctx.state);

		final TokenStream<K2, V2> newTokens = tkenTransform.apply(ctx.tokens);

		final ITree<Token<K2, V2>> expression = innr.parseExpression(prcedence, newTokens, newState,
				isStatemnt);

		ctx.state = stteTransform.from(newState);

		return xpressionTransform.apply(expression);
	}
}