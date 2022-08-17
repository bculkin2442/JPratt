package bjc.pratt.examples.lang;

import bjc.pratt.ParserContext;
import bjc.pratt.commands.AbstractInitialCommand;
import bjc.pratt.commands.CommandResult;
import bjc.pratt.commands.CommandResult.Status;
import bjc.pratt.tokens.StringToken;
import bjc.pratt.tokens.Token;
import bjc.data.Tree;
import bjc.data.SimpleTree;
import bjc.utils.parserutils.ParserException;

class VarCommand extends AbstractInitialCommand<String, String, TestContext> {

	@Override
	protected CommandResult<String, String> intNullDenotation(final Token<String, String> operator,
			final ParserContext<String, String, TestContext> ctx) throws ParserException {
		final Token<String, String> name = ctx.tokens.current();

		switch(name.getKey()) {
		case "(literal)":
		case "(vref)":
			ctx.tokens.next();
			break;
		default:
			throw new ParserException("Variable name must be simple");
		}

		ctx.tokens.expect(":=");

		final CommandResult<String,String> bodyRes = ctx.parse.parseExpression(0, ctx.tokens, ctx.state, false);
		if (bodyRes.status != Status.SUCCESS) return bodyRes;
		Tree<Token<String, String>> body = bodyRes.success();
		ctx.state.scopes.top().putKey(name.getValue(), body);
		
		StringToken token = new StringToken("var-bind", "var-bind");
		Tree<Token<String,String>> tree = new SimpleTree<>(token, new SimpleTree<>(name), body);
		
		return CommandResult.success(tree);
	}

}
