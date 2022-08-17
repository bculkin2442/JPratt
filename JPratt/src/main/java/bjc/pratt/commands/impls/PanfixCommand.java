package bjc.pratt.commands.impls;

import bjc.data.SimpleTree;
import bjc.data.Tree;
import bjc.pratt.ParserContext;
import bjc.pratt.commands.CommandResult;
import bjc.pratt.commands.InitialCommand;
import bjc.pratt.commands.CommandResult.Status;
import bjc.pratt.tokens.Token;
import bjc.utils.parserutils.ParserException;

public final class PanfixCommand<K, V, C> implements InitialCommand<K, V, C> {
	private final Token<K, V> marker;
	private final K term;
	private final int precedence;

	public PanfixCommand(Token<K, V> marker, K term, int precedence) {
		this.marker = marker;
		this.term = term;
		this.precedence = precedence;
	}

	@Override
	public CommandResult<K, V> denote(Token<K, V> operator, ParserContext<K, V, C> ctx) throws ParserException {
		CommandResult<K,V> resLeftSide = ctx.parse.parseExpression(precedence + 1, ctx.tokens, ctx.state, false);
		if (resLeftSide.status != Status.SUCCESS) return resLeftSide;
		Tree<Token<K, V>> leftSide = resLeftSide.success();
		ctx.tokens.expect(term);
		ctx.tokens.next();
		
		CommandResult<K, V> resRightSide = ctx.parse.parseExpression(precedence + 1, ctx.tokens, ctx.state, false);
		if (resLeftSide.status != Status.SUCCESS) return resRightSide;
		Tree<Token<K,V>> rightSide = resRightSide.success();
		ctx.tokens.expect(term);
		ctx.tokens.next();
		
		return CommandResult.success(new SimpleTree<>(marker, leftSide, rightSide));
	}
}