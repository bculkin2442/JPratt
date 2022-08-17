package bjc.pratt.commands;

import bjc.data.Tree;
import bjc.pratt.tokens.Token;

/**
 * Represents the result of executing a command.
 * 
 * @author bjcul
 *
 * @param <K> The key type of the tokens
 * @param <V> The value type of the tokens
 */
public class CommandResult<K, V> {
	/**
	 * Represents the status of a command execution
	 * 
	 * @author bjcul
	 *
	 */
	public static enum Status {
		/**
		 * The command successfully parsed.
		 */
		SUCCESS,
		/**
		 * The command failed, in a non-recoverable way
		 */
		FAIL,
		/**
		 * The command failed. Attempt recovery via backtracking
		 */
		BACKTRACK
	}

	/**
	 * The status of this command.
	 */
	public final Status status;
	
	private Tree<Token<K, V>> success;
	
	private CommandResult(Status status) {
		this.status = status;
	}

	/**
	 * Get the success value of this command, or null if it failed.
	 * 
	 * @return The success value of the command
	 */
	public Tree<Token<K, V>> success() {
		return success;
	}

	/**
	 * Create a success result
	 * 
	 * @param <K2> The key type of the token
	 * @param <V2> The value type of the token
	 * 
	 * @param succ The tree produced by the command
	 * 
	 * @return A command result representing a success
	 */
	public static <K2, V2> CommandResult<K2, V2> success(Tree<Token<K2, V2>> succ) {
		CommandResult<K2, V2> result = new CommandResult<>(Status.SUCCESS);
		result.success = succ;
		return result;
	}

	/**
	 * Create a non-backtracking failure result.
	 * 
	 * @param <K2> The key type of the token
	 * @param <V2> The value type of the token
	 * 
	 * @return A command result representing a non-backtracking fail
	 */
	public static <K2, V2> CommandResult<K2, V2> fail() {
		CommandResult<K2, V2> result = new CommandResult<>(Status.FAIL);
		return result;
	}

	/**
	 * Create a backtracking failure result.
	 * 
	 * @param <K2> The key type of the token
	 * @param <V2> The value type of the token
	 * 
	 * @return A command result representing a backtracking fail
	 */
	public static <K2, V2> CommandResult<K2, V2> backtrack() {
		CommandResult<K2, V2> result = new CommandResult<>(Status.BACKTRACK);
		return result;
	}
}
