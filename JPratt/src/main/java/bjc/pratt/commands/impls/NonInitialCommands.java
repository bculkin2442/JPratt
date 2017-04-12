package bjc.pratt.commands.impls;

import java.util.Set;

import bjc.pratt.blocks.ParseBlock;
import bjc.pratt.blocks.SimpleParseBlock;
import bjc.pratt.commands.NonInitialCommand;
import bjc.pratt.tokens.Token;

/**
 * Contains factory methods for producing common implementations of
 * {@link NonInitialCommand}
 *
 * @author EVE
 *
 */
public class NonInitialCommands {
	/**
	 * Create a left-associative infix operator.
	 *
	 * @param precedence
	 *                The precedence of the operator.
	 *
	 * @return A command implementing that operator.
	 */
	public static <K, V, C> NonInitialCommand<K, V, C> infixLeft(final int precedence) {
		return new LeftBinaryCommand<>(precedence);
	}

	/**
	 * Create a right-associative infix operator.
	 *
	 * @param precedence
	 *                The precedence of the operator.
	 *
	 * @return A command implementing that operator.
	 */
	public static <K, V, C> NonInitialCommand<K, V, C> infixRight(final int precedence) {
		return new RightBinaryCommand<>(precedence);
	}

	/**
	 * Create a non-associative infix operator.
	 *
	 * @param precedence
	 *                The precedence of the operator.
	 *
	 * @return A command implementing that operator.
	 */
	public static <K, V, C> NonInitialCommand<K, V, C> infixNon(final int precedence) {
		return new NonBinaryCommand<>(precedence);
	}

	/**
	 * Create a chained operator.
	 *
	 * @param precedence
	 *                The precedence of the operator.
	 *
	 * @param chainSet
	 *                The operators it forms a chain with.
	 *
	 * @param marker
	 *                The token to use as the AST node for the chained
	 *                operators.
	 *
	 * @return A command implementing that operator.
	 */
	public static <K, V, C> NonInitialCommand<K, V, C> chain(final int precedence, final Set<K> chainSet,
			final Token<K, V> marker) {
		return new ChainCommand<>(precedence, chainSet, marker);
	}

	/**
	 * Create a postfix operator.
	 *
	 * @param precedence
	 *                The precedence of the operator.
	 *
	 * @return A command implementing that operator.
	 */
	public static <K, V, C> NonInitialCommand<K, V, C> postfix(final int precedence) {
		return new PostfixCommand<>(precedence);
	}

	/**
	 * Create a post-circumfix operator.
	 *
	 * This is an operator in form similar to array indexing.
	 *
	 * @param precedence
	 *                The precedence of this operator
	 *
	 * @param insidePrecedence
	 *                The precedence of the expression inside the operator
	 *
	 * @param closer
	 *                The token that closes the circumfix.
	 *
	 * @param marker
	 *                The token to use as the AST node for the operator.
	 *
	 * @return A command implementing that operator.
	 */
	public static <K, V, C> NonInitialCommand<K, V, C> postCircumfix(final int precedence,
			final int insidePrecedence, final K closer, final Token<K, V> marker) {
		final ParseBlock<K, V, C> innerBlock = new SimpleParseBlock<>(insidePrecedence, null, closer);

		return new PostCircumfixCommand<>(precedence, innerBlock, marker);
	}

	/**
	 * Create a ternary operator.
	 *
	 * This is like C's ?: operator.
	 *
	 * @param precedence
	 *                The precedence of the operator.
	 *
	 * @param insidePrecedence
	 *                The precedence of the inner section of the operator.
	 *
	 * @param closer
	 *                The token that marks the end of the inner section.
	 *
	 * @param marker
	 *                The token to use as the AST node for the operator.
	 *
	 * @param nonassoc
	 *                True if the command is non-associative, false
	 *                otherwise.
	 *
	 * @return A command implementing this operator.
	 */
	public static <K, V, C> NonInitialCommand<K, V, C> ternary(final int precedence, final int insidePrecedence,
			final K closer, final Token<K, V> marker, final boolean nonassoc) {
		final ParseBlock<K, V, C> innerBlock = new SimpleParseBlock<>(insidePrecedence, null, closer);

		return new TernaryCommand<>(precedence, innerBlock, marker, nonassoc);
	}
}
