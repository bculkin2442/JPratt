package bjc.pratt.blocks;

import java.util.Set;

import bjc.pratt.ParserContext;
import bjc.pratt.commands.CommandResult;
import bjc.pratt.commands.CommandResult.Status;
import bjc.pratt.tokens.Token;
import bjc.data.Tree;
import bjc.data.SimpleTree;
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
	public CommandResult<K, V> parse(ParserContext<K, V, C> ctx) throws ParserException {
		CommandResult<K,V> resOuter = iner.parse(ctx);
		if (resOuter.status != Status.SUCCESS) return resOuter;
		
		Tree<Token<K, V>> expression = resOuter.success();
		Token<K, V> currentToken = ctx.tokens.current();
		if(indicators.contains(currentToken.getKey())) {
			Tree<Token<K, V>> res = new SimpleTree<>(trm);
			res.addChild(expression);

			while(indicators.contains(currentToken.getKey())) {
				res.addChild(new SimpleTree<>(currentToken));
				ctx.tokens.next();

				CommandResult<K,V> resInner = iner.parse(ctx);
				if (resInner.status != Status.SUCCESS) return resInner;
				
				Tree<Token<K, V>> innerExpression = resInner.success();
				res.addChild(innerExpression);

				currentToken = ctx.tokens.current();
			}

			return CommandResult.success(res);
		}

		return resOuter;
	}

}
