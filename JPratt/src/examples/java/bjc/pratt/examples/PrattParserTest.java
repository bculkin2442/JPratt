package bjc.pratt.examples;

import bjc.pratt.InitialCommand;
import bjc.pratt.NonInitialCommand;
import bjc.pratt.PrattParser;
import bjc.pratt.Token;
import bjc.pratt.tokens.StringToken;
import bjc.pratt.tokens.StringTokenStream;
import bjc.utils.data.ITree;
import bjc.utils.parserutils.ParserException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.function.UnaryOperator;

import static bjc.pratt.commands.InitialCommands.*;
import static bjc.pratt.commands.NonInitialCommands.*;
import static bjc.pratt.tokens.StringToken.litToken;
import static bjc.utils.functypes.ID.id;

/**
 * Simple test for Pratt parser.
 * 
 * @author EVE
 *
 */
public class PrattParserTest {
	/**
	 * Main method.
	 * 
	 * @param args
	 *            Unused CLI arguments.
	 */
	public static void main(String[] args) {
		InputState state = InputState.createState();

		PrattParser<String, String, TestContext> parser = createParser();

		Scanner scn = new Scanner(System.in);

		System.out.print("Enter a command (blank line to exit): ");
		String ln = scn.nextLine();

		while (!ln.trim().equals("")) {
			state.ln = ln;

			Iterator<Token<String, String>> tokens = state.preprocessInput();

			try {
				StringTokenStream tokenStream = new StringTokenStream(tokens);

				/*
				 * Prime stream.
				 */
				tokenStream.next();

				ITree<Token<String, String>> tree = parser.parseExpression(0, tokenStream, state.ctx, true);

				if (!tokenStream.headIs("(end)")) {
					System.out.println("\nMultiple expressions on line");
				}

				System.out.println("\nParsed expression:\n" + tree);
			} catch (ParserException pex) {
				pex.printStackTrace();
			}

			System.out.print("\nEnter a command (blank line to exit): ");
			ln = scn.nextLine();
		}

		System.out.println();
		System.out.println("\nContext is: " + state.ctx);

		scn.close();
	}

	private static PrattParser<String, String, TestContext> createParser() {
		/*
		 * Set of which relational operators chain with each other.
		 */
		HashSet<String> relChain = new HashSet<>();
		relChain.addAll(Arrays.asList("=", "<", ">", "<=", ">="));

		/*
		 * Token for marking chains.
		 */
		StringToken chainToken = litToken("and");

		/*
		 * ID function.
		 */
		UnaryOperator<TestContext> idfun = id();

		PrattParser<String, String, TestContext> parser = new PrattParser<>();

		parser.addNonInitialCommand("!!!", postfix(0));

		parser.addNonInitialCommand(":", infixNon(3));

		NonInitialCommand<String, String, TestContext> ifElse = ternary(5, 0, "else", litToken("cond"), false);
		parser.addNonInitialCommand("if", ifElse);

		parser.addNonInitialCommand(":=", new AssignCommand(10));

		parser.addNonInitialCommand("->", infixRight(11));

		NonInitialCommand<String, String, TestContext> nonSSRelJoin = infixLeft(13);
		parser.addNonInitialCommand("and", nonSSRelJoin);
		parser.addNonInitialCommand("or", nonSSRelJoin);

		NonInitialCommand<String, String, TestContext> chainRelOp = chain(15, relChain, chainToken);
		parser.addNonInitialCommand("=", chainRelOp);
		parser.addNonInitialCommand("<", chainRelOp);
		parser.addNonInitialCommand(">", chainRelOp);
		parser.addNonInitialCommand("<=", chainRelOp);
		parser.addNonInitialCommand(">=", chainRelOp);

		NonInitialCommand<String, String, TestContext> ssRelJoin = infixRight(17);
		parser.addNonInitialCommand("&&", ssRelJoin);
		parser.addNonInitialCommand("||", ssRelJoin);

		NonInitialCommand<String, String, TestContext> addSub = infixLeft(20);
		parser.addNonInitialCommand("+", addSub);
		parser.addNonInitialCommand("-", addSub);
		parser.addNonInitialCommand("±", addSub);

		NonInitialCommand<String, String, TestContext> mulDiv = infixLeft(30);
		parser.addNonInitialCommand("*", mulDiv);
		parser.addNonInitialCommand("/", mulDiv);

		parser.addNonInitialCommand("!", postfix(40));

		NonInitialCommand<String, String, TestContext> expon = infixRight(50);
		parser.addNonInitialCommand("^", expon);
		parser.addNonInitialCommand("root", expon);

		NonInitialCommand<String, String, TestContext> superexpon = postfix(50);
		parser.addNonInitialCommand("(superexp)", superexpon);

		parser.addNonInitialCommand(".", infixLeft(60));

		NonInitialCommand<String, String, TestContext> arrayIdx = postCircumfix(60, 0, "]", litToken("idx"));
		parser.addNonInitialCommand("[", arrayIdx);

		InitialCommand<String, String, TestContext> ifThenElse = preTernary(0, 0, 0, "then", "else",
				litToken("ifelse"));
		parser.addInitialCommand("if", ifThenElse);

		InitialCommand<String, String, TestContext> parens = grouping(0, ")", litToken("parens"));
		parser.addInitialCommand("(", parens);

		InitialCommand<String, String, TestContext> scoper = delimited(0, ";", "end", litToken("block"),
				new BlockEnter(), idfun, new BlockExit(), true);
		parser.addInitialCommand("begin", scoper);

		InitialCommand<String, String, TestContext> arrayLiteral = delimited(0, ",", "]", litToken("array"), idfun,
				idfun, idfun, false);
		parser.addInitialCommand("[", arrayLiteral);

		InitialCommand<String, String, TestContext> jsonLiteral = delimited(0, ",", "}", litToken("json"), idfun, idfun,
				idfun, false);
		parser.addInitialCommand("{", jsonLiteral);

		parser.addInitialCommand("case", unary(5));

		parser.addInitialCommand("-", unary(30));

		InitialCommand<String, String, TestContext> root = unary(50);
		parser.addInitialCommand("sqrt", root);
		parser.addInitialCommand("cbrt", root);

		InitialCommand<String, String, TestContext> leaf = leaf();
		parser.addInitialCommand("(literal)", leaf);

		parser.addInitialCommand("var", new VarCommand());

		parser.addInitialCommand("switch", new SwitchCommand());

		return parser;
	}
}