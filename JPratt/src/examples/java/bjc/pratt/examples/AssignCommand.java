package bjc.pratt.examples;

import bjc.pratt.ParserContext;
import bjc.pratt.Token;
import bjc.pratt.commands.NonBinaryCommand;
import bjc.pratt.tokens.StringToken;
import bjc.utils.data.ITree;
import bjc.utils.data.Tree;
import bjc.utils.parserutils.ParserException;

class AssignCommand extends NonBinaryCommand<String, String, TestContext> {
	public AssignCommand(int prec) {
		super(prec);
	}

	@Override
	public ITree<Token<String, String>> denote(ITree<Token<String, String>> operand, Token<String, String> operator,
			ParserContext<String, String, TestContext> ctx) throws ParserException {
		Token<String, String> name = operand.getHead();

		switch (name.getKey()) {
		case "(literal)":
		case "(vref)":
			break;
		default:
			throw new ParserException("Variable name must be simple");
		}

		ITree<Token<String, String>> body = ctx.parse.parseExpression(0, ctx.tokens, ctx.state, false);

		ctx.state.scopes.top().putKey(name.getValue(), body);

		return new Tree<>(new StringToken("assign", "assign"), operand, body);
	}
}
