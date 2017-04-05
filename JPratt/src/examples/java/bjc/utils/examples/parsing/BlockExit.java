package bjc.utils.examples.parsing;

import java.util.function.UnaryOperator;

final class BlockExit implements UnaryOperator<TestContext> {
	@Override
	public TestContext apply(TestContext state) {
		state.scopes.pop();

		state.blockCount.pop();

		return state;
	}
}