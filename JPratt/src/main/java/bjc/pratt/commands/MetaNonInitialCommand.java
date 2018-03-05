package bjc.pratt.commands;

import bjc.pratt.ParserContext;

/**
 * A 'meta-command' for non-initial commands.
 * 
 * @author bjculkin
 *
 * @param <K>
 *            The token key type.
 * 
 * @param <V>
 *            The token value type.
 * 
 * @param <C>
 *            The parser state type.
 */
public interface MetaNonInitialCommand<K, V, C> {
	/**
	 * Get the command to use.
	 * 
	 * @param ctx
	 *            The context to use.
	 * @return The command to use.
	 */
	NonInitialCommand<K, V, C> getCommand(ParserContext<K, V, C> ctx);
}
