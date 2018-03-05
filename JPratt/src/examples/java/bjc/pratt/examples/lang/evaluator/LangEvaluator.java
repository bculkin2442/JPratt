package bjc.pratt.examples.lang.evaluator;

import bjc.pratt.examples.lang.ast.LangAST;
import bjc.utils.data.ITree;

/**
 * Evaluate an AST.
 * 
 * @author student
 *
 */
public class LangEvaluator {
	/**
	 * Evaluate an AST
	 * 
	 * @param ast
	 *            The AST to evaluate
	 * @return The result of evaluation.
	 */
	public LangResult evaluate(ITree<LangAST> ast) {
		ITree<LangAST> evaluatedTree = ast.topDownTransform(LangAST::getEvaluationStrategy, this::evaluateNode);

		return evaluatedTree.getHead().toResult();
	}

	private ITree<LangAST> evaluateNode(ITree<LangAST> node) {
		return node;
	}
}