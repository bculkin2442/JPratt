package bjc.pratt.commands.impls;

import java.util.function.UnaryOperator;

import bjc.pratt.ParserContext;
import bjc.pratt.commands.AbstractInitialCommand;
import bjc.pratt.commands.CommandResult;
import bjc.pratt.commands.CommandResult.Status;
import bjc.pratt.commands.InitialCommand;
import bjc.pratt.tokens.Token;
import bjc.data.Tree;
import bjc.utils.parserutils.ParserException;

/**
 * An initial command that transforms the result of another command.
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
public class TransformingInitialCommand<K, V, C> extends AbstractInitialCommand<K, V, C> {
	private final InitialCommand<K, V, C> internl;

	private final UnaryOperator<Tree<Token<K, V>>> transfrm;

	/**
	 * Create a new transforming initial command.
	 *
	 * @param internal
	 *        The initial command to delegate to.
	 *
	 * @param transform
	 *        The transform to apply to the returned tree.
	 */
	public TransformingInitialCommand(final InitialCommand<K, V, C> internal,
			final UnaryOperator<Tree<Token<K, V>>> transform) {
		super();
		internl = internal;
		transfrm = transform;
	}

	@Override
	protected CommandResult<K, V> intNullDenotation(final Token<K, V> operator, final ParserContext<K, V, C> ctx)
			throws ParserException {
		CommandResult<K,V> result = internl.denote(operator, ctx);
		if (result.status != Status.SUCCESS) return result;
		
		return CommandResult.success(transfrm.apply(result.success()));
	}

}
