package bjc.pratt.examples;

import bjc.pratt.Token;
import bjc.utils.data.ITree;
import bjc.utils.esodata.Directory;

import java.util.function.UnaryOperator;

final class BlockEnter implements UnaryOperator<TestContext> {
	@Override
	public TestContext apply(TestContext state) {
		Directory<String, ITree<Token<String, String>>> enclosing = state.scopes.top();
		int currBlockNumber = state.blockCount.pop();

		state.scopes.push(enclosing.newSubdirectory("block" + currBlockNumber));

		state.blockCount.push(currBlockNumber + 1);
		state.blockCount.push(0);

		return state;
	}
}