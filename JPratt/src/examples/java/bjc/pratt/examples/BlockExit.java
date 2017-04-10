package bjc.pratt.examples;

import java.util.function.UnaryOperator;

final class BlockExit implements UnaryOperator<TestContext> {
	@Override
	public TestContext apply(final TestContext state) {
		state.scopes.pop();

		state.blockCount.pop();

		return state;
	}
}