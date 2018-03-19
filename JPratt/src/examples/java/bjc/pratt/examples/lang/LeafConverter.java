package bjc.pratt.examples.lang;

import java.util.function.Function;

import bjc.pratt.examples.lang.ast.LangAST;
import bjc.pratt.examples.lang.ast.LiteralAST;
import bjc.pratt.tokens.Token;

final class LeafConverter implements Function<Token<String, String>, LangAST> {
	@Override
	public LangAST apply(Token<String, String> leaf) {
		return LiteralAST.fromToken(leaf.getValue());
	}
}