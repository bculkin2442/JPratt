package bjc.pratt;

/**
 * A 'meta-command' for non-initial commands.
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
public interface MetaNonInitialCommand<K, V, C> {
	NonInitialCommand<K, V, C> getCommand(ParserContext<K, V, C> ctx);
}
