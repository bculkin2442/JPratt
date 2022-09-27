package bjc.pratt.blocks;

import java.util.function.UnaryOperator;

import bjc.pratt.ParserContext;
import bjc.pratt.commands.CommandResult;
import bjc.pratt.commands.CommandResult.Status;
import bjc.utils.parserutils.ParserException;

/**
 * A parse block that can adjust the state before handling its context.
 *
 * @author bjculkin
 *
 * @param <K>
 *        The key type of the tokens.
 * @param <V>
 *        The value type of the tokens.
 * @param <C>
 *        The state type of the parser.
 */
public class TriggeredParseBlock<K, V, C> implements ParseBlock<K, V, C> {
	private final UnaryOperator<C> onEntr;
	private final UnaryOperator<C> onExt;

	private final ParseBlock<K, V, C> sourc;

	/**
	 * Create a new triggered parse block.
	 *
	 * @param onEnter
	 *        The action to fire before parsing the block.
	 *
	 * @param onExit
	 *        The action to fire after parsing the block.
	 *
	 * @param source
	 *        The block to use for parsing.
	 */
	public TriggeredParseBlock(final UnaryOperator<C> onEnter, final UnaryOperator<C> onExit,
			final ParseBlock<K, V, C> source) {
		onEntr = onEnter;
		onExt = onExit;
		sourc = source;
	}

	@Override
	public CommandResult<K, V> parse(final ParserContext<K, V, C> ctx) throws ParserException {
		final C newState = onEntr.apply(ctx.state);

		final ParserContext<K, V, C> newCtx = new ParserContext<>(ctx.tokens, ctx.parse, newState);

		final CommandResult<K,V> res = sourc.parse(newCtx);

		if (res.status != Status.SUCCESS) return res;
		
		ctx.state = onExt.apply(newState);

		return res;
	}

}
