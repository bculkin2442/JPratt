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
	private PrattParser<K2, V2, C2> inner;

	private int	precedence;
	private boolean	isStatement;

	private Function<TokenStream<K, V>, TokenStream<K2, V2>>	tokenTransform;
	private Isomorphism<C, C2>					stateTransform;
	private Function<ITree<Token<K2, V2>>, ITree<Token<K, V>>>	expressionTransform;

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
	public GrammarParseBlock(PrattParser<K2, V2, C2> inner, int precedence, boolean isStatement,
			Function<TokenStream<K, V>, TokenStream<K2, V2>> tokenTransform,
			Isomorphism<C, C2> stateTransform,
			Function<ITree<Token<K2, V2>>, ITree<Token<K, V>>> expressionTransform) {
		this.inner = inner;
		this.precedence = precedence;
		this.isStatement = isStatement;
		this.tokenTransform = tokenTransform;
		this.stateTransform = stateTransform;
		this.expressionTransform = expressionTransform;
	}

	@Override
	public ITree<Token<K, V>> parse(ParserContext<K, V, C> ctx) throws ParserException {
		C2 newState = stateTransform.to(ctx.state);

		TokenStream<K2, V2> newTokens = tokenTransform.apply(ctx.tokens);

		ITree<Token<K2, V2>> expression = inner.parseExpression(precedence, newTokens, newState, isStatement);

		ctx.state = stateTransform.from(newState);

		return expressionTransform.apply(expression);
	}
}