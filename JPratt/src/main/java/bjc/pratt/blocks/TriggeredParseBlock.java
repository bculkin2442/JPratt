package bjc.pratt.blocks;

import java.util.function.UnaryOperator;

import bjc.pratt.ParseBlock;
import bjc.pratt.ParserContext;
import bjc.pratt.Token;
import bjc.utils.data.ITree;
import bjc.utils.parserutils.ParserException;

/**
 * A parse block that can adjust the state before handling its context.
 * 
 * @author bjculkin
 *
 * @param <K>
 *                The key type of the tokens.
 * @param <V>
 *                The value type of the tokens.
 * @param <C>
 *                The state type of the parser.
 */
public class TriggeredParseBlock<K, V, C> implements ParseBlock<K, V, C> {
	private UnaryOperator<C>	onEntr;
	private UnaryOperator<C>	onExt;

	private ParseBlock<K, V, C> sourc;

	/**
	 * Create a new triggered parse block.
	 * 
	 * @param onEnter
	 *                The action to fire before parsing the block.
	 * 
	 * @param onExit
	 *                The action to fire after parsing the block.
	 * 
	 * @param source
	 *                The block to use for parsing.
	 */
	public TriggeredParseBlock(UnaryOperator<C> onEnter, UnaryOperator<C> onExit, ParseBlock<K, V, C> source) {
		onEntr = onEnter;
		onExt = onExit;
		sourc = source;
	}

	@Override
	public ITree<Token<K, V>> parse(ParserContext<K, V, C> ctx) throws ParserException {
		C newState = onEntr.apply(ctx.state);

		ParserContext<K, V, C> newCtx = new ParserContext<>(ctx.tokens, ctx.parse, newState);

		ITree<Token<K, V>> res = sourc.parse(newCtx);

		ctx.state = onExt.apply(newState);

		return res;
	}

}
