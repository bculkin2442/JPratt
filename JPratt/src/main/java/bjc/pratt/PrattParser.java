package bjc.pratt;

import java.util.HashMap;
import java.util.Map;

import bjc.pratt.commands.DefaultInitialCommand;
import bjc.pratt.commands.DefaultNonInitialCommand;
import bjc.utils.data.ITree;
import bjc.utils.funcutils.NumberUtils;
import bjc.utils.parserutils.ParserException;

/**
 * A configurable Pratt parser for expressions.
 *
 * @author EVE
 *
 * @param <K>
 *                The key type for the tokens.
 *
 * @param <V>
 *                The value type for the tokens.
 *
 * @param <C>
 *                The state type of the parser.
 *
 *
 */
public class PrattParser<K, V, C> {
	/*
	 * Default commands that error when used.
	 */
	private final NonInitialCommand<K, V, C>	DEFAULT_LEFT_COMMAND	= new DefaultNonInitialCommand<>();
	private final InitialCommand<K, V, C>		DEFAULT_NULL_COMMAND	= new DefaultInitialCommand<>();

	/*
	 * Left-commands that depend on what the null command was.
	 */
	private final Map<K, Map<K, NonInitialCommand<K, V, C>>> dependantLeftCommands;

	/*
	 * The left commands.
	 */
	private final Map<K, NonInitialCommand<K, V, C>> leftCommands;
	/*
	 * The initial commands.
	 */
	private final Map<K, InitialCommand<K, V, C>> nullCommands;
	/*
	 * Initial commands only checked for statements.
	 */
	private final Map<K, InitialCommand<K, V, C>> statementCommands;

	/**
	 * Create a new Pratt parser.
	 *
	 */
	public PrattParser() {
		dependantLeftCommands = new HashMap<>();

		leftCommands = new HashMap<>();
		nullCommands = new HashMap<>();
		statementCommands = new HashMap<>();
	}

	/**
	 * Parse an expression.
	 *
	 * @param precedence
	 *                The initial precedence for the expression.
	 *
	 * @param tokens
	 *                The tokens for the expression.
	 *
	 * @param state
	 *                The state of the parser.
	 *
	 * @param isStatement
	 *                Whether or not to parse statements.
	 *
	 * @return The expression as an AST.
	 *
	 * @throws ParserException
	 *                 If something goes wrong during parsing.
	 */
	public ITree<Token<K, V>> parseExpression(final int precedence, final TokenStream<K, V> tokens, final C state,
			final boolean isStatement) throws ParserException {
		if (precedence < 0) throw new IllegalArgumentException("Precedence must be greater than zero");

		final Token<K, V> initToken = tokens.current();
		tokens.next();

		final K initKey = initToken.getKey();

		ITree<Token<K, V>> ast;

		if (isStatement && statementCommands.containsKey(initKey)) {
			ast = statementCommands.getOrDefault(initKey, DEFAULT_NULL_COMMAND).denote(initToken,
					new ParserContext<>(tokens, this, state));
		} else {
			ast = nullCommands.getOrDefault(initKey, DEFAULT_NULL_COMMAND).denote(initToken,
					new ParserContext<>(tokens, this, state));
		}

		int rightPrec = Integer.MAX_VALUE;

		while (true) {
			final Token<K, V> tok = tokens.current();

			final K key = tok.getKey();

			NonInitialCommand<K, V, C> command = leftCommands.getOrDefault(key, DEFAULT_LEFT_COMMAND);

			if (dependantLeftCommands.containsKey(initKey)) {
				command = dependantLeftCommands.get(initKey).getOrDefault(key, command);
			}

			final int leftBind = command.leftBinding();

			if (NumberUtils.between(precedence, rightPrec, leftBind)) {
				tokens.next();

				ast = command.denote(ast, tok, new ParserContext<>(tokens, this, state));
				rightPrec = command.nextBinding();
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
	 *                The key that marks the command.
	 *
	 * @param comm
	 *                The command.
	 */
	public void addNonInitialCommand(final K marker, final NonInitialCommand<K, V, C> comm) {
		leftCommands.put(marker, comm);
	}

	/**
	 * Add a initial command to this parser.
	 *
	 * @param marker
	 *                The key that marks the command.
	 *
	 * @param comm
	 *                The command.
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
	 *                The key that marks the command.
	 *
	 * @param comm
	 *                The command.
	 */
	public void addStatementCommand(final K marker, final InitialCommand<K, V, C> comm) {
		statementCommands.put(marker, comm);
	}

	/**
	 * Add a dependent non-initial command to this parser.
	 *
	 * @param dependant
	 *                The dependent that precedes the command.
	 *
	 * @param marker
	 *                The token key that marks the command.
	 *
	 * @param comm
	 *                The command.
	 */
	public void addDependantCommand(final K dependant, final K marker, final NonInitialCommand<K, V, C> comm) {
		if (dependantLeftCommands.containsKey(dependant)) {
			dependantLeftCommands.get(dependant).put(marker, comm);
		} else {
			final Map<K, NonInitialCommand<K, V, C>> comms = new HashMap<>();

			comms.put(marker, comm);

			dependantLeftCommands.put(dependant, comms);
		}
	}
}