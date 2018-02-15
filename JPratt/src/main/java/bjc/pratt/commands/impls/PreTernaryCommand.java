package bjc.pratt.commands.impls;

import bjc.pratt.ParserContext;
import bjc.pratt.blocks.ParseBlock;
import bjc.pratt.commands.AbstractInitialCommand;
import bjc.pratt.tokens.Token;
import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;

/**
 * A prefix ternary operator, like an if/then/else group.
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
public class PreTernaryCommand<K, V, C> extends AbstractInitialCommand<K, V, C> {
	private final Token<K, V> trm;

	private final ParseBlock<K, V, C> condBlock;

	private final ParseBlock<K, V, C>	opblock1;
	private final ParseBlock<K, V, C>	opblock2;

	/**
	 * Create a new ternary statement.
	 *
	 * @param cond
	 *        The block for handling the condition.
	 *
	 * @param op1
	 *        The block for handling the first operator.
	 *
	 * @param op2
	 *        The block for handling the second operator.
	 *
	 * @param term
	 *        The token to use as the node for the AST.
	 */
	public PreTernaryCommand(final ParseBlock<K, V, C> cond, final ParseBlock<K, V, C> op1,
			final ParseBlock<K, V, C> op2, final Token<K, V> term) {
		super();

		if(cond == null)
			throw new NullPointerException("Cond block must not be null");
		else if(op1 == null)
			throw new NullPointerException("Op block #1 must not be null");
		else if(op2 == null) throw new NullPointerException("Op block #2 must not be null");

		condBlock = cond;
		opblock1 = op1;
		opblock2 = op2;

		trm = term;
	}

	@Override
	protected ITree<Token<K, V>> intNullDenotation(final Token<K, V> operator, final ParserContext<K, V, C> ctx)
			throws ParserException {
		final ITree<Token<K, V>> cond = condBlock.parse(ctx);

		final ITree<Token<K, V>> op1 = opblock1.parse(ctx);

		final ITree<Token<K, V>> op2 = opblock2.parse(ctx);

		return new Tree<>(trm, cond, op1, op2);
	}
}