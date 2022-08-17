package bjc.pratt.commands.impls;

import static bjc.pratt.blocks.ParseBlocks.repeating;
import static bjc.pratt.blocks.ParseBlocks.simple;
import static bjc.pratt.blocks.ParseBlocks.trigger;

import java.util.function.UnaryOperator;

import bjc.pratt.blocks.ParseBlock;
import bjc.pratt.commands.InitialCommand;
import bjc.pratt.tokens.Token;
import bjc.utils.parserutils.ParserException;
import bjc.data.SimpleTree;
import bjc.data.Tree;

/**
 * * Contains factory methods for producing common implementations of
 * {@link InitialCommand}
 *
 * @author EVE
 *
 */
public class InitialCommands {
	/**
	 * Create a new unary operator.
	 * 
	 * @param <K> The key type for the tokens. 
	 * @param <V> The value type for the tokens.
	 * @param <C> The context type for the tokens.
	 *
	 * @param precedence
	 *        The precedence of the operator.
	 *
	 * @return A command implementing that operator.
	 */
	public static <K, V, C> InitialCommand<K, V, C> unary(final int precedence) {
		return new UnaryCommand<>(precedence);
	}

	/**
	 * Create a new grouping operator.
	 *
	 * @param <K> The key type for the tokens. 
	 * @param <V> The value type for the tokens.
	 * @param <C> The context type for the tokens.
	 *
	 * @param precedence
	 *        The precedence of the expression in the operator.
	 *
	 * @param term
	 *        The type that closes the group.
	 *
	 * @param mark
	 *        The token for the AST node of the group.
	 *
	 * @return A command implementing the operator.
	 */
	public static <K, V, C> InitialCommand<K, V, C> grouping(final int precedence, final K term,
			final Token<K, V> mark) {
		final ParseBlock<K, V, C> innerBlock = simple(precedence, term, null);

		return new GroupingCommand<>(innerBlock, mark);
	}

	/**
	 * Create a new leaf operator.
	 * 
	 * @param <K> The key type for the tokens. 
	 * @param <V> The value type for the tokens.
	 * @param <C> The context type for the tokens.
	 *
	 * @return A command implementing the operator.
	 */
	public static <K, V, C> InitialCommand<K, V, C> leaf() {
		return new LeafCommand<>();
	}

	/**
	 * Create a new pre-ternary operator, like an if-then-else statement.
	 *
	 * @param <K> The key type for the tokens. 
	 * @param <V> The value type for the tokens.
	 * @param <C> The context type for the tokens.
	 *
	 * @param cond1
	 *        The priority of the first block.
	 *
	 * @param block1
	 *        The priority of the second block.
	 *
	 * @param block2
	 *        The priority of the third block.
	 *
	 * @param mark1
	 *        The marker that ends the first block.
	 *
	 * @param mark2
	 *        The marker that ends the second block.
	 *
	 * @param term
	 *        The token for the AST node of the group.
	 *
	 * @return A command implementing the operator.
	 */
	public static <K, V, C> InitialCommand<K, V, C> preTernary(final int cond1, final int block1, final int block2,
			final K mark1, final K mark2, final Token<K, V> term) {
		final ParseBlock<K, V, C> condBlock = simple(cond1, mark1, null);
		final ParseBlock<K, V, C> opblock1 = simple(block1, mark2, null);
		final ParseBlock<K, V, C> opblock2 = simple(block2, null, null);

		return new PreTernaryCommand<>(condBlock, opblock1, opblock2, term);
	}

	/**
	 * Create a new named constant.
	 *
	 * @param <K> The key type for the tokens. 
	 * @param <V> The value type for the tokens.
	 * @param <C> The context type for the tokens.
	 *
	 * @param val
	 *        The value of the constant.
	 *
	 * @return A command implementing the constant.
	 */
	public static <K, V, C> InitialCommand<K, V, C> constant(final Tree<Token<K, V>> val) {
		return new ConstantCommand<>(val);
	}

	/**
	 * Create a new delimited command. This is for block-like constructs.
	 *
	 * @param <K> The key type for the tokens. 
	 * @param <V> The value type for the tokens.
	 * @param <C> The context type for the tokens.
	 *
	 * @param inner
	 *        The precedence of the inner blocks.
	 *
	 * @param delim
	 *        The marker between sub-blocks.
	 *
	 * @param mark
	 *        The block terminator.
	 *
	 * @param term
	 *        The token for the AST node of the group.
	 *
	 * @param onEnter
	 *        The function to apply to the state on entering the block.
	 *
	 * @param onDelim
	 *        The function to apply to the state on finishing a sub-block.
	 *
	 * @param onExit
	 *        The function to apply to the state on exiting the block.
	 *
	 * @param statement
	 *        Whether or not the sub-blocks are statements or expressions.
	 *
	 * @return A command implementing the operator.
	 */
	public static <K, V, C> InitialCommand<K, V, C> delimited(final int inner, final K delim, final K mark,
			final Token<K, V> term, final UnaryOperator<C> onEnter, final UnaryOperator<C> onDelim,
			final UnaryOperator<C> onExit, final boolean statement) {
		final ParseBlock<K, V, C> innerBlock = simple(inner, null, null);
		final ParseBlock<K, V, C> delimsBlock = repeating(innerBlock, delim, mark, term, onDelim);
		final ParseBlock<K, V, C> scopedBlock = trigger(delimsBlock, onEnter, onExit);

		final GroupingCommand<K, V, C> command = new GroupingCommand<>(scopedBlock, term);

		/*
		 * Remove the wrapper layer from grouping-command on top of
		 * RepeatingParseBlock.
		 */
		return denest(command);
	}

	/**
	 * Create a new denesting command.
	 *
	 * This removes one tree-level, and is useful when combining complex
	 * parse blocks with commands.
	 *
	 * @param <K> The key type for the tokens. 
	 * @param <V> The value type for the tokens.
	 * @param <C> The context type for the tokens.
	 *
	 * @param comm
	 *        The command to denest.
	 *
	 * @return A command that denests the result of the provided command.
	 */
	public static <K, V, C> InitialCommand<K, V, C> denest(final InitialCommand<K, V, C> comm) {
		return new DenestingCommand<>(comm);
	}
	
	/**
	 * Create a new 'panfix' command.
	 * 
	 * Form is <term> <expr> <term> <expr> <term>
	 * 
	 * @param <K> The key type for the tokens. 
	 * @param <V> The value type for the tokens.
	 * @param <C> The context type for the tokens.
	 * 
	 * @param precedence The precedence for this operator
	 * @param term The indicator for the operator
	 * @param marker The token to mark this tree
	 * 
	 * @return A command that implements a panfix operator
	 */
	public static <K, V, C> InitialCommand<K, V, C> panfix(final int precedence, final K term, final Token<K, V> marker) {
		return new PanfixCommand<K, V, C>(marker, term, precedence);
	}
}