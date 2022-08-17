package bjc.pratt.blocks;

import bjc.pratt.ParserContext;
import bjc.pratt.commands.CommandResult;
import bjc.pratt.tokens.Token;
import bjc.data.Tree;
import bjc.utils.parserutils.ParserException;

/**
 * Represents a embedded block in an expression.
 *
 * @author bjculkin
 *
 * @param <K>
 *        The key type of the token.
 *
 * @param <V>
 *        The value type of the token.
 *
 * @param <C>
 *        The state type of the parser.
 */
@FunctionalInterface
public interface ParseBlock<K, V, C> {

	/**
	 * Parse the block this represents.
	 *
	 * @param ctx
	 *        The context for parsing.
	 *
	 * @return A AST for this block.
	 *
	 * @throws ParserException
	 *         If something goes wrong during parsing, or the block fails
	 *         validation.
	 */
	CommandResult<K, V> parse(ParserContext<K, V, C> ctx) throws ParserException;
}