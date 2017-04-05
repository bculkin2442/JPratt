package bjc.pratt.commands;

import bjc.pratt.InitialCommand;
import bjc.pratt.ParserContext;
import bjc.pratt.Token;
import bjc.utils.data.ITree;
import bjc.utils.parserutils.ParserException;

/**
 * A command that represents a specific tree.
 * 
 * @author bjculkin
 *
 * @param <K>
 *                The key type of the tokens.
 * 
 * @param <V>
 *                The value type of the tokens.
 * 
 * @param <C>
 *                The state type of the parser.
 */
public class ConstantCommand<K, V, C> implements InitialCommand<K, V, C> {
	private ITree<Token<K, V>> val;

	/**
	 * Create a new constant.
	 * 
	 * @param con
	 *                The tree this constant represents.
	 */
	public ConstantCommand(ITree<Token<K, V>> con) {
		val = con;
	}

	@Override
	public ITree<Token<K, V>> denote(Token<K, V> operator, ParserContext<K, V, C> ctx) throws ParserException {
		return val;
	}
}