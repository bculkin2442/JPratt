package bjc.pratt.examples.lang;

import bjc.pratt.ParserContext;
import bjc.pratt.commands.CommandResult;
import bjc.pratt.commands.CommandResult.Status;
import bjc.pratt.commands.InitialCommand;
import bjc.pratt.tokens.StringToken;
import bjc.pratt.tokens.Token;
import bjc.data.Tree;
import bjc.data.SimpleTree;
import bjc.utils.parserutils.ParserException;

class SwitchCommand implements InitialCommand<String, String, TestContext> {
	@Override
	public CommandResult<String, String> denote(final Token<String, String> operator,
			final ParserContext<String, String, TestContext> ctx) throws ParserException {
		final CommandResult<String,String> objectRes = ctx.parse.parseExpression(0, ctx.tokens, ctx.state, false);
		if (objectRes.status != Status.SUCCESS) return objectRes;
		Tree<Token<String, String>> object = objectRes.success();
		
		final CommandResult<String,String> bodyRes = ctx.parse.parseExpression(0, ctx.tokens, ctx.state, false);
		if (bodyRes.status != Status.SUCCESS) return bodyRes;
		Tree<Token<String, String>> body = bodyRes.success();
		
		return CommandResult.success(new SimpleTree<>(new StringToken("switch", "switch"), object, body));
	}
}
