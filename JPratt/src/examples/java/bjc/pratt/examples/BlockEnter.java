package bjc.pratt.examples;

import java.util.function.UnaryOperator;

import bjc.pratt.Token;
import bjc.utils.data.ITree;
import bjc.utils.esodata.Directory;
import bjc.utils.esodata.Stack;

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