package bjc.pratt.examples;

import bjc.utils.data.ITree;
import bjc.utils.data.TopDownTransformResult;

/**
 * Evaluate a tree to a result.
 * 
 * @author student
 *
 */
public class TreeEvaluator {
	/**
	 * The result of evaluating a tree.
	 * 
	 * @author student
	 *
	 */
	public static final class EvaluationResult {
		public static EvaluationResult fromToken(EvaluationToken tok) {
			return null;
		}
	}

	/**
	 * The token type for trees to evaluate.
	 * 
	 * @author student
	 *
	 */
	public static final class EvaluationToken {

	}

	public static EvaluationResult evaluateTree(ITree<EvaluationToken> tree, TestContext ctx) {
		ITree<EvaluationToken> result = tree.topDownTransform(TreeEvaluator::pickNodeEvaluation,
				TreeEvaluator::evaluateNode);

		return EvaluationResult.fromToken(result.getHead());
	}

	private static TopDownTransformResult pickNodeEvaluation(EvaluationToken node) {
		return TopDownTransformResult.PUSHDOWN;
	}

	private static ITree<EvaluationToken> evaluateNode(ITree<EvaluationToken> tree) {
		return tree;
	}
}
