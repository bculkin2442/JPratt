package bjc.pratt.commands;

import bjc.pratt.NonInitialCommand;
import bjc.pratt.ParseBlock;
import bjc.pratt.ParserContext;
import bjc.pratt.Token;
import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;

/**
 * A non-initial command that delegates all of the work to a {@link ParseBlock}
 * 
 * @author bjculkin
 *
 * @param <K>
 *                The token key type.
 * 
 * @param <V>
 *                The token value type.
 * 
 * @param <C>
 *                The parser state type.
 */
public class BlockNonInitialCommand<K, V, C> extends NonInitialCommand<K, V, C> {
	private ParseBlock<K, V, C> innr;

	private int	lftBind;
	private int	nxtBind;

	private Token<K, V> trm;

	/**
	 * Create a new non-initial command that delegates to a parse block.
	 * 
	 * @param inner
	 *                The parse block to delegate to.
	 * 
	 * @param leftBind
	 *                The left binding power (precedence).
	 * 
	 * @param rightBind
	 *                The right binding power (associativity control).
	 * 
	 * @param term
	 *                The token to use as the node in the AST.
	 */
	public BlockNonInitialCommand(ParseBlock<K, V, C> inner, int leftBind, int rightBind, Token<K, V> term) {
		innr = inner;

		lftBind = leftBind;
		nxtBind = rightBind;

		trm = term;
	}

	@Override
	public ITree<Token<K, V>> denote(ITree<Token<K, V>> operand, Token<K, V> operator, ParserContext<K, V, C> ctx)
			throws ParserException {
		ITree<Token<K, V>> expression = innr.parse(ctx);

		return new Tree<>(trm, expression);
	}

	@Override
	public int leftBinding() {
		return lftBind;
	}

	@Override
	public int nextBinding() {
		return nxtBind;
	}
}