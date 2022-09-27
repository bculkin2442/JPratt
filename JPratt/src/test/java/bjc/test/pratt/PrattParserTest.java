package bjc.test.pratt;

import static org.junit.Assert.*;
import static bjc.test.pratt.TestUtils.*;

import java.util.Iterator;

import org.junit.Test;

import bjc.data.ArrayIterator;
import bjc.data.SimpleTree;
import bjc.data.Tree;
import bjc.pratt.PrattParser;
import bjc.pratt.commands.CommandResult;
import bjc.pratt.commands.impls.InitialCommands;
import bjc.pratt.commands.impls.NonInitialCommands;
import bjc.pratt.tokens.StringTokenStream;
import bjc.pratt.tokens.Token;
import bjc.utils.parserutils.ParserException;

@SuppressWarnings("javadoc")
public class PrattParserTest {

	@Test
	public void test() {
		Iterator<Token<String, String>> iter = new ArrayIterator<>(token("(int)", "1"), token("+"),
				token("(int)", "2"));
		StringTokenStream tokens = new StringTokenStream(iter);

		PrattParser<String, String, TestState> parser = new PrattParser<>();

		parser.addInitialCommand("(int)", InitialCommands.leaf());
		parser.addNonInitialCommand("+", NonInitialCommands.infixLeft(1));

		try {			
			TestState state = new TestState();
			
			tokens.mark();
			CommandResult<String, String> result = parser.parseExpression(0, tokens, state, false);
			tokens.rollback();
			
			assertEquals(CommandResult.Status.SUCCESS, result.status);
			Tree<Token<String, String>> actualTree = result.success();

			Tree<Token<String, String>> expectedTree = new SimpleTree<>(token("+"),
					new SimpleTree<>(token("(int)", "1")), new SimpleTree<>(token("(int)", "2")));
			
			assertEquals(expectedTree, actualTree);
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

}

class TestState {

}
