package bjc.pratt.commands.impls;

import bjc.pratt.ParserContext;
import bjc.pratt.blocks.ParseBlock;
import bjc.pratt.commands.AbstractInitialCommand;
import bjc.pratt.tokens.Token;
import bjc.data.Tree;
import bjc.utils.parserutils.ParserException;

/**
 * An initial command that delegates all the work to a {@link ParseBlock}
 *
 * @author bjculkin
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
public class BlockInitialCommand<K, V, C> extends AbstractInitialCommand<K, V, C> {
	private final ParseBlock<K, V, C> blck;

	/**
	 * Create a new block initial command.
	 *
	 * @param block
	 *        The block to delegate to.
	 */
	public BlockInitialCommand(final ParseBlock<K, V, C> block) {
		blck = block;
	}

	@Override
	protected Tree<Token<K, V>> intNullDenotation(final Token<K, V> operator, final ParserContext<K, V, C> ctx)
			throws ParserException {
		return blck.parse(ctx);
	}
}