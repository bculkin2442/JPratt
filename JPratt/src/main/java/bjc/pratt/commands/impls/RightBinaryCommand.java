package bjc.pratt.commands.impls;

import bjc.pratt.commands.BinaryCommand;

/**
 * A right-associative binary operator.
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
public class RightBinaryCommand<K, V, C> extends BinaryCommand<K, V, C> {
	/**
	 * Create a new right-associative operator.
	 *
	 * @param precedence
	 *        The precedence of the operator.
	 */
	public RightBinaryCommand(final int precedence) {
		super(precedence);
	}

	@Override
	protected int rightBinding() {
		return leftBinding();
	}
}