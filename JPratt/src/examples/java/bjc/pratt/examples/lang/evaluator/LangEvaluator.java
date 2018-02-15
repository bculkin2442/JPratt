package bjc.pratt.examples.lang.evaluator;

import bjc.pratt.examples.lang.ast.LangAST;
import bjc.utils.data.ITree;

public class LangEvaluator {
	public LangResult evaluate(ITree<LangAST> ast) {
		ITree<LangAST> evaluatedTree = ast.topDownTransform(LangAST::getEvaluationStrategy, this::evaluateNode);

		return evaluatedTree.getHead().toResult();
	}

	private ITree<LangAST> evaluateNode(ITree<LangAST> node) {
		return node;
	}
}