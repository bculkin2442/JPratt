package bjc.pratt.commands;

import bjc.pratt.ParseBlock;
import bjc.pratt.ParserContext;
import bjc.pratt.Token;
import bjc.utils.data.ITree;
import bjc.utils.parserutils.ParserException;

/**
 * An initial command that delegates all the work to a {@link ParseBlock}
 * 
 * @author bjculkin
 * @param <K>
 *                The token key type.
 * 
 * @param <V>
 *                The token value type.
 * 
 * @param <C>
 *                The parser state type.
 *
 */
public class BlockInitialCommand<K, V, C> extends AbstractInitialCommand<K, V, C> {
	private ParseBlock<K, V, C> blck;

	/**
	 * Create a new block initial command.
	 * 
	 * @param block
	 *                The block to delegate to.
	 */
	public BlockInitialCommand(ParseBlock<K, V, C> block) {
		blck = block;
	}

	@Override
	protected ITree<Token<K, V>> intNullDenotation(Token<K, V> operator, ParserContext<K, V, C> ctx)
			throws ParserException {
		return blck.parse(ctx);
	}
}