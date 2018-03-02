package bjc.pratt;

import java.util.HashMap;
import java.util.Map;

import bjc.pratt.commands.InitialCommand;
import bjc.pratt.commands.MetaInitialCommand;
import bjc.pratt.commands.MetaNonInitialCommand;
import bjc.pratt.commands.NonInitialCommand;
import bjc.pratt.commands.impls.DefaultInitialCommand;
import bjc.pratt.commands.impls.DefaultNonInitialCommand;
import bjc.pratt.tokens.Token;
import bjc.pratt.tokens.TokenStream;
import bjc.utils.data.ITree;
import bjc.utils.funcutils.NumberUtils;
import bjc.utils.parserutils.ParserException;

/**
 * A configurable Pratt parser for expressions.
 *
 * @author EVE
 *
 * @param <K>
 *        The key type for the tokens.
 *
 * @param <V>
 *        The value type for the tokens.
 *
 * @param <C>
 *        The state type of the parser.
 *
 *
 */
public class PrattParser<K, V, C> {
	/*
	 * Default commands that error when used.
	 */
	private final NonInitialCommand<K, V, C> DEFAULT_LEFT_COMMAND = new DefaultNonInitialCommand<>();
	private final InitialCommand<K, V, C> DEFAULT_NULL_COMMAND = new DefaultInitialCommand<>();

	/*
	 * Left-commands that depend on what the null command was.
	 */
	private final Map<K, Map<K, NonInitialCommand<K, V, C>>> dependantLeftCommands;
	private final Map<K, Map<K, MetaNonInitialCommand<K, V, C>>> dependantMetaLeftCommands;
	/*
	 * The left commands.
	 */
	private final Map<K, NonInitialCommand<K, V, C>> leftCommands;
	private final Map<K, MetaNonInitialCommand<K, V, C>> metaLeftCommands;

	/*
	 * The initial commands.
	 */
	private final Map<K, InitialCommand<K, V, C>> nullCommands;
	private final Map<K, MetaInitialCommand<K, V, C>> metaNullCommands;

	/*
	 * Initial commands only checked for statements.
	 */
	private final Map<K, InitialCommand<K, V, C>> statementCommands;
	private final Map<K, MetaInitialCommand<K, V, C>> metaStatementCommands;

	/**
	 * Create a new Pratt parser.
	 *
	 */
	public PrattParser() {
		dependantLeftCommands = new HashMap<>();
		dependantMetaLeftCommands = new HashMap<>();

		leftCommands = new HashMap<>();
		metaLeftCommands = new HashMap<>();

		nullCommands = new HashMap<>();
		metaNullCommands = new HashMap<>();

		statementCommands = new HashMap<>();
		metaStatementCommands = new HashMap<>();
	}

	/**
	 * Parse an expression.
	 *
	 * @param precedence
	 *        The initial precedence for the expression.
	 *
	 * @param tokens
	 *        The tokens for the expression.
	 *
	 * @param state
	 *        The state of the parser.
	 *
	 * @param isStatement
	 *        Whether or not to parse statements.
	 *
	 * @return The expression as an AST.
	 *
	 * @throws ParserException
	 *         If something goes wrong during parsing.
	 */
	public ITree<Token<K, V>> parseExpression(final int precedence, final TokenStream<K, V> tokens, final C state,
			final boolean isStatement) throws ParserException {
		if(precedence < 0) throw new IllegalArgumentException("Precedence must be greater than zero");

		ParserContext<K, V, C> parserContext = new ParserContext<>(tokens, this, state);

		final Token<K, V> initToken = tokens.current();
		tokens.next();

		final K initKey = initToken.getKey();

		InitialCommand<K, V, C> nullCommand = getInitialCommand(isStatement, initKey, parserContext);
		ITree<Token<K, V>> ast = nullCommand.denote(initToken, parserContext);

		parserContext.initial = initKey;

		int rightPrec = Integer.MAX_VALUE;

		while(true) {
			final Token<K, V> tok = tokens.current();

			final K key = tok.getKey();

			NonInitialCommand<K, V, C> leftCommand = getNonInitialCommand(key, parserContext);

			final int leftBind = leftCommand.leftBinding();

			if(NumberUtils.between(precedence, rightPrec, leftBind)) {
				tokens.next();

				ast = leftCommand.denote(ast, tok, parserContext);
				rightPrec = leftCommand.nextBinding();
			} else {
				break;
			}
		}

		return ast;
	}

	/**
	 * Add a non-initial command to this parser.
	 *
	 * @param marker
	 *        The key that marks the command.
	 *
	 * @param comm
	 *        The command.
	 */
	public void addNonInitialCommand(final K marker, final NonInitialCommand<K, V, C> comm) {
		leftCommands.put(marker, comm);
	}

	/**
	 * Add a initial command to this parser.
	 *
	 * @param marker
	 *        The key that marks the command.
	 *
	 * @param comm
	 *        The command.
	 */
	public void addInitialCommand(final K marker, final InitialCommand<K, V, C> comm) {
		nullCommands.put(marker, comm);
	}

	/**
	 * Add a statement command to this parser.
	 *
	 * The difference between statements and initial commands is that
	 * statements can only appear at the start of the expression.
	 *
	 * @param marker
	 *        The key that marks the command.
	 *
	 * @param comm
	 *        The command.
	 */
	public void addStatementCommand(final K marker, final InitialCommand<K, V, C> comm) {
		statementCommands.put(marker, comm);
	}

	/**
	 * Add a dependent non-initial command to this parser.
	 *
	 * @param dependant
	 *        The dependent that precedes the command.
	 *
	 * @param marker
	 *        The token key that marks the command.
	 *
	 * @param comm
	 *        The command.
	 */
	public void addDependantCommand(final K dependant, final K marker, final NonInitialCommand<K, V, C> comm) {
		Map<K, NonInitialCommand<K, V, C>> dependantMap = dependantLeftCommands.getOrDefault(dependant,
				new HashMap<>());

		dependantMap.put(marker, comm);
	}

	/**
	 * Lookup an initial command.
	 * 
	 * @param isStatement
	 *        Whether to look for statement commands or not.
	 * 
	 * @param key
	 *        The key of the command.
	 * 
	 * @param ctx
	 *        The context for meta-commands.
	 * 
	 * @return A command attached to that key, or a default implementation.
	 */
	public InitialCommand<K, V, C> getInitialCommand(boolean isStatement, K key, ParserContext<K, V, C> ctx) {
		if(isStatement) {
			if(metaStatementCommands.containsKey(key))
				return metaStatementCommands.get(key).getCommand(ctx);
			else if(statementCommands.containsKey(key)) return statementCommands.get(key);
		}

		if(metaNullCommands.containsKey(key)) {
			return metaNullCommands.get(key).getCommand(ctx);
		}

		return nullCommands.getOrDefault(key, DEFAULT_NULL_COMMAND);
	}

	/**
	 * Lookup a non-initial command.
	 * 
	 * @param key
	 *        The key of the command.
	 * 
	 * @param ctx
	 *        The context for meta-commands.
	 * 
	 * @return A command attached to that key, or a default implementation.
	 */
	public NonInitialCommand<K, V, C> getNonInitialCommand(K key, ParserContext<K, V, C> ctx) {
		if(dependantMetaLeftCommands.containsKey(ctx.initial)) {
			Map<K, MetaNonInitialCommand<K, V, C>> dependantCommands = dependantMetaLeftCommands
					.get(ctx.initial);

			if(dependantCommands.containsKey(key)) {
				return dependantCommands.get(key).getCommand(ctx);
			}
		}

		if(dependantLeftCommands.containsKey(ctx.initial)) {
			Map<K, NonInitialCommand<K, V, C>> dependantCommands = dependantLeftCommands.get(ctx.initial);

			if(dependantCommands.containsKey(key)) {
				return dependantCommands.getOrDefault(key, DEFAULT_LEFT_COMMAND);
			}
		}

		if(metaLeftCommands.containsKey(key)) {
			return metaLeftCommands.get(key).getCommand(ctx);
		}

		return leftCommands.getOrDefault(key, DEFAULT_LEFT_COMMAND);
	}
}