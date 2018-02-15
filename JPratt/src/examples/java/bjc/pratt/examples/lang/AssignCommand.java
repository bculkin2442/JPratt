package bjc.pratt.examples.lang;

import bjc.pratt.ParserContext;
import bjc.pratt.commands.impls.NonBinaryCommand;
import bjc.pratt.tokens.StringToken;
import bjc.pratt.tokens.Token;
import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;

class AssignCommand extends NonBinaryCommand<String, String, TestContext> {
	public AssignCommand(final int prec) {
		super(prec);
	}

	@Override
	public ITree<Token<String, String>> denote(final ITree<Token<String, String>> operand,
			final Token<String, String> operator, final ParserContext<String, String, TestContext> ctx)
			throws ParserException {
		final Token<String, String> name = operand.getHead();

		switch(name.getKey()) {
		case "(literal)":
		case "(vref)":
			break;
		default:
			throw new ParserException("Variable name must be simple");
		}

		final ITree<Token<String, String>> body = ctx.parse.parseExpression(0, ctx.tokens, ctx.state, false);

		ctx.state.scopes.top().putKey(name.getValue(), body);

		return new Tree<>(new StringToken("assign", "assign"), operand, body);
	}
}
