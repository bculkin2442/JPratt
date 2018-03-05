package bjc.pratt.commands;

import bjc.pratt.ParserContext;

/**
 * A 'meta-command' that yields the actual command to use.
 * 
 * @author bjculkin
 * 
 * @param <K>
 *            The key type of the context.
 * @param <V>
 *            The value type of the context.
 * @param <C>
 *            The storage type of the context.
 *
 */
public interface MetaInitialCommand<K, V, C> {
	/**
	 * Get the command to use.
	 * 
	 * @param ctx
	 *            The current parser context.
	 * @return The command to use.
	 */
	InitialCommand<K, V, C> getCommand(ParserContext<K, V, C> ctx);
}
