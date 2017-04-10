package bjc.pratt.examples;

import bjc.pratt.Token;
import bjc.utils.data.ITree;
import bjc.utils.esodata.Directory;
import bjc.utils.esodata.Stack;

import java.util.function.UnaryOperator;

final class BlockEnter implements UnaryOperator<TestContext> {
	@Override
	public TestContext apply(TestContext state) {
		Directory<String, ITree<Token<String, String>>> enclosing = state.scopes.top();
		Stack<Integer> blockCount = state.blockCount;

		int currBlockNumber = blockCount.pop();

		state.scopes.push(enclosing.newSubdirectory("block" + currBlockNumber));

		blockCount.push(currBlockNumber + 1);
		blockCount.push(0);

		return state;
	}
}