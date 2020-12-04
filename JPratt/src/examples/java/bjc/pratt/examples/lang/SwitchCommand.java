package bjc.pratt.examples.lang;

import bjc.pratt.ParserContext;
import bjc.pratt.commands.InitialCommand;
import bjc.pratt.tokens.StringToken;
import bjc.pratt.tokens.Token;
import bjc.data.Tree;
import bjc.data.SimpleTree;
import bjc.utils.parserutils.ParserException;

class SwitchCommand implements InitialCommand<String, String, TestContext> {
	@Override
	public Tree<Token<String, String>> denote(final Token<String, String> operator,
			final ParserContext<String, String, TestContext> ctx) throws ParserException {
		final Tree<Token<String, String>> object = ctx.parse.parseExpression(0, ctx.tokens, ctx.state, false);

		final Tree<Token<String, String>> body = ctx.parse.parseExpression(0, ctx.tokens, ctx.state, false);

		return new SimpleTree<>(new StringToken("switch", "switch"), object, body);
	}
}
