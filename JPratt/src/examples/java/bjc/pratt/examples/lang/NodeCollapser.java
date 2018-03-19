package bjc.pratt.examples.lang;

import java.util.function.BiFunction;

import bjc.pratt.examples.lang.ast.LangAST;
import bjc.pratt.tokens.Token;
import bjc.utils.funcdata.IList;

final class NodeCollapser implements BiFunction<Token<String, String>, IList<LangAST>, LangAST> {
	@Override
	public LangAST apply(Token<String, String> token, IList<LangAST> children) {
		// TODO Auto-generated method stub
		return null;
	}
}