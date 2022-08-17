package bjc.pratt.commands.impls;

import bjc.pratt.ParserContext;
import bjc.pratt.blocks.ParseBlock;
import bjc.pratt.commands.AbstractInitialCommand;
import bjc.pratt.commands.CommandResult;
import bjc.pratt.commands.CommandResult.Status;
import bjc.pratt.tokens.Token;
import bjc.data.Tree;
import bjc.data.SimpleTree;
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

	private final ParseBlock<K, V, C> opblock1;
	private final ParseBlock<K, V, C> opblock2;

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
	protected CommandResult<K, V> intNullDenotation(final Token<K, V> operator, final ParserContext<K, V, C> ctx)
			throws ParserException {
		final CommandResult<K,V> condRes = condBlock.parse(ctx);
		if (condRes.status != Status.SUCCESS) return condRes;
		Tree<Token<K, V>> cond = condRes.success();
		
		final CommandResult<K,V> op1Res = opblock1.parse(ctx);
		if (op1Res.status != Status.SUCCESS) return op1Res;
		Tree<Token<K, V>> op1 = op1Res.success();

		final CommandResult<K,V> op2Res = opblock2.parse(ctx);
		if (op2Res.status != Status.SUCCESS) return op2Res;
		Tree<Token<K, V>> op2 = op2Res.success();
		return CommandResult.success(new SimpleTree<>(trm, cond, op1, op2));
	}
}