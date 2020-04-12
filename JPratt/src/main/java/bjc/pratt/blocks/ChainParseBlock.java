package bjc.pratt.blocks;

import java.util.Set;

import bjc.pratt.ParserContext;
import bjc.pratt.tokens.Token;
import bjc.data.ITree;
import bjc.data.Tree;
import bjc.utils.parserutils.ParserException;

/**
 * A {@link ParseBlock} for a series of parse blocks, linked by a set of tokens.
 * 
 * Roughly analogous to Perl 6s list associative operators.
 * 
 * @author bjculkin
 * 
 * @param <K>
 *        The token key type.
 * 
 * @param <V>
 *        The token value type.
 * 
 * @param <C>
 *        The parser state type.
 *
 */
public class ChainParseBlock<K, V, C> implements ParseBlock<K, V, C> {
	private ParseBlock<K, V, C> iner;

	private Set<K> indicators;

	private Token<K, V> trm;

	/**
	 * Create a new chain parser block.
	 * 
	 * @param inner
	 *        The block for the chains interior.
	 * 
	 * @param chainIndicators
	 *        The set of markers that indicate continuing the chain
	 * 
	 * @param term
	 *        The node in the AST for the expression.
	 */
	public ChainParseBlock(ParseBlock<K, V, C> inner, Set<K> chainIndicators, Token<K, V> term) {
		iner = inner;
		indicators = chainIndicators;
		trm = term;
	}

	@Override
	public ITree<Token<K, V>> parse(ParserContext<K, V, C> ctx) throws ParserException {
		ITree<Token<K, V>> expression = iner.parse(ctx);

		Token<K, V> currentToken = ctx.tokens.current();
		if(indicators.contains(currentToken.getKey())) {
			ITree<Token<K, V>> res = new Tree<>(trm);
			res.addChild(expression);

			while(indicators.contains(currentToken.getKey())) {
				res.addChild(new Tree<>(currentToken));
				ctx.tokens.next();

				ITree<Token<K, V>> innerExpression = iner.parse(ctx);
				res.addChild(innerExpression);

				currentToken = ctx.tokens.current();
			}

			return res;
		}

		return expression;
	}

}
