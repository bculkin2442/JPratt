package bjc.pratt.examples.lang;

import java.util.function.UnaryOperator;

import bjc.pratt.tokens.Token;
import bjc.data.ITree;
import bjc.esodata.Directory;
import bjc.esodata.Stack;

final class BlockEnter implements UnaryOperator<TestContext> {
	@Override
	public TestContext apply(final TestContext state) {
		final Directory<String, ITree<Token<String, String>>> enclosing = state.scopes.top();
		final Stack<Integer> blockCount = state.blockCount;

		final int currBlockNumber = blockCount.pop();

		state.scopes.push(enclosing.newSubdirectory("block" + currBlockNumber));

		blockCount.push(currBlockNumber + 1);
		blockCount.push(0);

		return state;
	}
}