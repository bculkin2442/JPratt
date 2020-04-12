package bjc.pratt.examples.lang;

import bjc.pratt.tokens.Token;
import bjc.data.ITree;
import bjc.esodata.Directory;
import bjc.esodata.SimpleDirectory;
import bjc.esodata.SimpleStack;
import bjc.esodata.Stack;

/**
 * Simple context for the parser.
 *
 * @author EVE
 *
 */
public class TestContext {
	/**
	 * The variable scoping information.
	 */
	public Stack<Directory<String, ITree<Token<String, String>>>> scopes;

	/**
	 * The current number of scopes inside this scope.
	 */
	public Stack<Integer> blockCount;

	/**
	 * Create a new test context.
	 */
	public TestContext() {
		scopes = new SimpleStack<>();
		blockCount = new SimpleStack<>();

		scopes.push(new SimpleDirectory<>());
		blockCount.push(0);
	}

	@Override
	public String toString() {
		return String.format("TestContext [scopes=%s\n, blockCount=%s]", scopes, blockCount);
	}
}
