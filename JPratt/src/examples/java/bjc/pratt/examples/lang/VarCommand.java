package bjc.pratt.examples.lang;

import bjc.pratt.ParserContext;
import bjc.pratt.commands.AbstractInitialCommand;
import bjc.pratt.tokens.StringToken;
import bjc.pratt.tokens.Token;
import bjc.data.ITree;
import bjc.data.Tree;
import bjc.utils.parserutils.ParserException;

class VarCommand extends AbstractInitialCommand<String, String, TestContext> {

	@Override
	protected ITree<Token<String, String>> intNullDenotation(final Token<String, String> operator,
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

		final ITree<Token<String, String>> body = ctx.parse.parseExpression(0, ctx.tokens, ctx.state, false);

		ctx.state.scopes.top().putKey(name.getValue(), body);

		return new Tree<>(new StringToken("var-bind", "var-bind"), new Tree<>(name), body);
	}

}
