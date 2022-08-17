package bjc.pratt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import bjc.pratt.commands.CommandResult;
import bjc.pratt.commands.CommandResult.Status;
import bjc.pratt.commands.InitialCommand;
import bjc.pratt.commands.MetaInitialCommand;
import bjc.pratt.commands.MetaNonInitialCommand;
import bjc.pratt.commands.NonInitialCommand;
import bjc.pratt.commands.impls.DefaultInitialCommand;
import bjc.pratt.commands.impls.DefaultNonInitialCommand;
import bjc.pratt.tokens.ExpectionNotMet;
import bjc.pratt.tokens.Token;
import bjc.pratt.tokens.TokenStream;
import bjc.data.TransformIterator;
import bjc.data.Tree;
import bjc.utils.funcutils.NumberUtils;
import bjc.utils.parserutils.ParserException;

/**
 * A configurable Pratt parser for expressions.
 *
 * @author EVE
 *
 * @param <K> The key type for the tokens.
 *
 * @param <V> The value type for the tokens.
 *
 * @param <C> The state type of the parser.
 *
 *
 */
public class PrattParser<K, V, C> {
	/*
	 * Default commands that error when used.
	 */
	private final NonInitialCommand<K, V, C> DEFAULT_LEFT_COMMAND = new DefaultNonInitialCommand<>();
	private final List<NonInitialCommand<K, V, C>> DEFAULT_LEFT_LIST = Arrays.asList(DEFAULT_LEFT_COMMAND);

	private final InitialCommand<K, V, C> DEFAULT_NULL_COMMAND = new DefaultInitialCommand<>();
	private final List<InitialCommand<K, V, C>> DEFAULT_NULL_LIST = Arrays.asList(DEFAULT_NULL_COMMAND);

	/*
	 * Left-commands that depend on what the null command was.
	 */
	private final Map<K, Map<K, List<NonInitialCommand<K, V, C>>>> dependantLeftCommands;
	private final Map<K, Map<K, List<MetaNonInitialCommand<K, V, C>>>> dependantMetaLeftCommands;
	/*
	 * The left commands.
	 */
	private final Map<K, List<NonInitialCommand<K, V, C>>> leftCommands;
	private final Map<K, List<MetaNonInitialCommand<K, V, C>>> metaLeftCommands;

	/*
	 * The initial commands.
	 */
	private final Map<K, List<InitialCommand<K, V, C>>> nullCommands;
	private final Map<K, List<MetaInitialCommand<K, V, C>>> metaNullCommands;

	/*
	 * Initial commands only checked for statements.
	 */
	private final Map<K, List<InitialCommand<K, V, C>>> statementCommands;
	private final Map<K, List<MetaInitialCommand<K, V, C>>> metaStatementCommands;

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
	 * @param precedence  The initial precedence for the expression.
	 *
	 * @param tokens      The tokens for the expression.
	 *
	 * @param state       The state of the parser.
	 *
	 * @param isStatement Whether or not to parse statements.
	 *
	 * @return The expression as an AST.
	 *
	 * @throws ParserException If something goes wrong during parsing.
	 */
	public CommandResult<K, V> parseExpression(final int precedence, final TokenStream<K, V> tokens, final C state,
			final boolean isStatement) throws ParserException {
		if (precedence < 0)
			throw new IllegalArgumentException("Precedence must be greater than zero");

		ParserContext<K, V, C> parserContext = new ParserContext<>(tokens, this, state);

		Tree<Token<K, V>> ast = null;
		CommandResult<K, V> result;

		tokens.mark();
		final Token<K, V> initToken = tokens.current();
		tokens.next();
		tokens.mark();

		final K initKey = initToken.getKey();
		Iterator<InitialCommand<K, V, C>> nullCommandIter = getInitialCommand(isStatement, initKey, parserContext);
		do {
			if (!nullCommandIter.hasNext()) {
				// Restore to the state we were in before we tried to parse this token.
				// Need the commit because rollback doesn't remove marks
				tokens.rollback();
				tokens.commit();
				tokens.rollback();
				return CommandResult.backtrack();
			}

			InitialCommand<K, V, C> nullCommand = nullCommandIter.next();
			try {
				result = nullCommand.denote(initToken, parserContext);
			} catch (ExpectionNotMet enm) {
				// TODO: Should enm be used for something here?
				result = CommandResult.backtrack();
			}
			switch (result.status) {
			case SUCCESS:
				ast = result.success();
				break;
			case FAIL:
				return result;
			case BACKTRACK:
				tokens.rollback();
				break;
			default:
				throw new IllegalStateException("Unhandled result status " + result.status);
			}
			parserContext.initial = initKey;
		} while (result.status != Status.SUCCESS);
		tokens.commit();
		// Think this is right...
		// Will get rid of all our active marks.
		tokens.commit();

		int rightPrec = Integer.MAX_VALUE;

		outer: while (true) {
			tokens.mark();
			final Token<K, V> tok = tokens.current();

			final K key = tok.getKey();

			Iterator<NonInitialCommand<K, V, C>> leftCommandIter = getNonInitialCommand(key, parserContext);
			do {
				if (!leftCommandIter.hasNext()) {
					// Restore to the state we were in before we tried to parse this token.
					// Need the commit because rollback doesn't remove marks
					tokens.rollback();
					tokens.commit();
					tokens.rollback();
					return CommandResult.backtrack();
				}

				NonInitialCommand<K, V, C> leftCommand = leftCommandIter.next();
				final int leftBind = leftCommand.leftBinding();

				if (NumberUtils.between(precedence, rightPrec, leftBind)) {
					tokens.next();
					tokens.mark();

					try {
						result = leftCommand.denote(ast, tok, parserContext);
						rightPrec = leftCommand.nextBinding();
					} catch (ExpectionNotMet enm) {
						result = CommandResult.backtrack();
					}

					switch (result.status) {
					case SUCCESS:
						tokens.commit();
						tokens.commit();
						ast = result.success();
						break;
					case FAIL:
						return result;
					case BACKTRACK:
						tokens.rollback();
						break;
					default:
						throw new IllegalStateException("Unhandled result status " + result.status);
					}
				} else {
					tokens.commit();
					break outer;
				}
			} while (result.status != Status.SUCCESS);
		}

		return CommandResult.success(ast);
	}

	/**
	 * Add a non-initial command to this parser.
	 *
	 * @param marker The key that marks the command.
	 *
	 * @param comm   The command.
	 */
	public void addNonInitialCommand(final K marker, final NonInitialCommand<K, V, C> comm) {
		leftCommands.computeIfAbsent(marker, mrk -> new ArrayList<>()).add(comm);
	}

	/**
	 * Add a initial command to this parser.
	 *
	 * @param marker The key that marks the command.
	 *
	 * @param comm   The command.
	 */
	public void addInitialCommand(final K marker, final InitialCommand<K, V, C> comm) {
		nullCommands.computeIfAbsent(marker, mrk -> new ArrayList<>()).add(comm);
	}

	/**
	 * Add a statement command to this parser.
	 *
	 * The difference between statements and initial commands is that statements can
	 * only appear at the start of the expression.
	 *
	 * @param marker The key that marks the command.
	 *
	 * @param comm   The command.
	 */
	public void addStatementCommand(final K marker, final InitialCommand<K, V, C> comm) {
		statementCommands.computeIfAbsent(marker, mrk -> new ArrayList<>()).add(comm);
	}

	/**
	 * Add a dependent non-initial command to this parser.
	 *
	 * @param dependant The dependent that precedes the command.
	 *
	 * @param marker    The token key that marks the command.
	 *
	 * @param comm      The command.
	 */
	public void addDependantCommand(final K dependant, final K marker, final NonInitialCommand<K, V, C> comm) {
		Map<K, List<NonInitialCommand<K, V, C>>> dependantMap = dependantLeftCommands.getOrDefault(dependant,
				new HashMap<>());

		dependantMap.computeIfAbsent(marker, mrk -> new ArrayList<>()).add(comm);
	}

	/**
	 * Lookup an initial command.
	 * 
	 * @param isStatement Whether to look for statement commands or not.
	 * 
	 * @param key         The key of the command.
	 * 
	 * @param ctx         The context for meta-commands.
	 * 
	 * @return A command attached to that key, or a default implementation.
	 */
	public Iterator<InitialCommand<K, V, C>> getInitialCommand(boolean isStatement, K key, ParserContext<K, V, C> ctx) {
		if (isStatement) {
			if (metaStatementCommands.containsKey(key)) {
				List<MetaInitialCommand<K, V, C>> lst = metaStatementCommands.get(key);

				return new TransformIterator<>(lst.iterator(), (itm) -> itm.getCommand(ctx));
			} else if (statementCommands.containsKey(key)) {
				return statementCommands.get(key).iterator();
			}
		}

		if (metaNullCommands.containsKey(key)) {
			List<MetaInitialCommand<K, V, C>> lst = metaNullCommands.get(key);

			return new TransformIterator<>(lst.iterator(), (itm) -> itm.getCommand(ctx));
		}

		return nullCommands.getOrDefault(key, DEFAULT_NULL_LIST).iterator();
	}

	/**
	 * Lookup a non-initial command.
	 * 
	 * @param key The key of the command.
	 * 
	 * @param ctx The context for meta-commands.
	 * 
	 * @return A command attached to that key, or a default implementation.
	 */
	public Iterator<NonInitialCommand<K, V, C>> getNonInitialCommand(K key, ParserContext<K, V, C> ctx) {
		if (dependantMetaLeftCommands.containsKey(ctx.initial)) {
			Map<K, List<MetaNonInitialCommand<K, V, C>>> dependantCommands = dependantMetaLeftCommands.get(ctx.initial);

			if (dependantCommands.containsKey(key)) {
				List<MetaNonInitialCommand<K, V, C>> lst = dependantCommands.get(key);

				return new TransformIterator<>(lst.iterator(), (itm) -> itm.getCommand(ctx));
			}
		}

		if (dependantLeftCommands.containsKey(ctx.initial)) {
			Map<K, List<NonInitialCommand<K, V, C>>> dependantCommands = dependantLeftCommands.get(ctx.initial);

			if (dependantCommands.containsKey(key)) {
				return dependantCommands.getOrDefault(key, DEFAULT_LEFT_LIST).iterator();
			}
		}

		if (metaLeftCommands.containsKey(key)) {
			List<MetaNonInitialCommand<K, V, C>> lst = metaLeftCommands.get(key);
			return new TransformIterator<>(lst.iterator(), (itm) -> itm.getCommand(ctx));
		}

		return leftCommands.getOrDefault(key, DEFAULT_LEFT_LIST).iterator();
	}
}