package bjc.pratt;

/**
 * A 'meta-command' that yields the actual command to use.
 * 
 * @author bjculkin
 *
 */
public interface MetaInitialCommand<K, V, C> {
	InitialCommand<K, V, C> getCommand(ParserContext<K, V, C> ctx);
}
