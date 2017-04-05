package bjc.pratt.examples;

import bjc.pratt.InitialCommand;
import bjc.pratt.ParserContext;
import bjc.pratt.Token;
import bjc.pratt.tokens.StringToken;
import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;

class SwitchCommand implements InitialCommand<String, String, TestContext> {
	@Override
	public ITree<Token<String, String>> denote(Token<String, String> operator,
			ParserContext<String, String, TestContext> ctx) throws ParserException {
		ITree<Token<String, String>> object = ctx.parse.parseExpression(0, ctx.tokens, ctx.state, false);

		ITree<Token<String, String>> body = ctx.parse.parseExpression(0, ctx.tokens, ctx.state, false);

		return new Tree<>(new StringToken("switch", "switch"), object, body);
	}
}
