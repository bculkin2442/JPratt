package bjc.pratt;

/**
 * Represents the contextual state passed to a command.
 *
 * @author EVE
 *
 * @param <K>
 *                The key type of the tokens.
 * @param <V>
 *                The value type of the tokens.
 *
 * @param <C>
 *                The state type of the parser.
 */
public class ParserContext<K, V, C> {
	/**
	 * The source of tokens.
	 */
	public TokenStream<K, V>	tokens;
	/**
	 * The parser for sub-expressions.
	 */
	public PrattParser<K, V, C>	parse;
	/**
	 * The state of the parser.
	 */
	public C			state;

	/**
	 * Create a new parser context.
	 *
	 * @param tokns
	 *                The source of tokens.
	 *
	 * @param prse
	 *                The parser to call for sub expressions.
	 *
	 * @param stte
	 *                Any state needing to be kept during parsing.
	 */
	public ParserContext(final TokenStream<K, V> tokns, final PrattParser<K, V, C> prse, final C stte) {
		this.tokens = tokns;
		this.parse = prse;
		this.state = stte;
	}
}