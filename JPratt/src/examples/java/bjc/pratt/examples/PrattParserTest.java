package bjc.pratt.examples;

import bjc.pratt.InitialCommand;
import bjc.pratt.NonInitialCommand;
import bjc.pratt.PrattParser;
import bjc.pratt.Token;
import bjc.pratt.tokens.StringToken;
import bjc.pratt.tokens.StringTokenStream;
import bjc.utils.data.ITree;
import bjc.utils.data.TransformIterator;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.splitter.TokenSplitter;
import bjc.utils.parserutils.splitter.TwoLevelSplitter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
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
	 *                Unused CLI arguments.
	 */
	public static void main(String[] args) {
		/*
		 * Use a linked hash set to preserve insertion order.
		 */
		Set<String> ops = new LinkedHashSet<>();

		ops.add("!!!");

		ops.addAll(Arrays.asList("->", "=>"));
		ops.add(":=");
		ops.addAll(Arrays.asList("||", "&&"));
		ops.addAll(Arrays.asList("<=", ">="));

		ops.addAll(Arrays.asList("±"));
		ops.addAll(Arrays.asList(".", ",", ";", ":"));
		ops.addAll(Arrays.asList("=", "<", ">"));
		ops.addAll(Arrays.asList("+", "-", "*", "/"));
		ops.addAll(Arrays.asList("^", "!"));
		ops.addAll(Arrays.asList("(", ")"));
		ops.addAll(Arrays.asList("[", "]"));
		ops.addAll(Arrays.asList("{", "}"));

		/*
		 * Reserved words that represent themselves, not literals.
		 */
		Set<String> reserved = new LinkedHashSet<>();
		reserved.addAll(Arrays.asList("if", "then", "else"));
		reserved.addAll(Arrays.asList("and", "or"));
		reserved.addAll(Arrays.asList("begin", "end"));
		reserved.addAll(Arrays.asList("switch", "case"));
		reserved.addAll(Arrays.asList("sqrt", "cbrt", "root"));
		reserved.add("var");

		TwoLevelSplitter split = new TwoLevelSplitter();

		split.addCompoundDelim("->", "=>");
		split.addCompoundDelim(":=");
		split.addCompoundDelim("||", "&&");
		split.addCompoundDelim("<=", ">=");

		split.addSimpleDelim("±");
		split.addSimpleDelim(".", ",", ";", ":");
		split.addSimpleDelim("=", "<", ">");
		split.addSimpleDelim("+", "-", "*", "/");
		split.addSimpleDelim("^", "!");

		split.addSimpleMulti("!");
		split.addSimpleMulti("\\(", "\\)");
		split.addSimpleMulti("\\[", "\\]");
		split.addSimpleMulti("\\{", "\\}");

		split.exclude(reserved.toArray(new String[0]));

		split.compile();

		PrattParser<String, String, TestContext> parser = createParser();

		TestContext ctx = new TestContext();

		Scanner scn = new Scanner(System.in);

		System.out.print("Enter a command (blank line to exit): ");
		String ln = scn.nextLine();

		while(!ln.trim().equals("")) {
			Iterator<Token<String, String>> tokens = preprocessInput(ops, split, ln, reserved, ctx);

			try {
				StringTokenStream tokenStream = new StringTokenStream(tokens);

				/*
				 * Prime stream.
				 */
				tokenStream.next();

				ITree<Token<String, String>> tree = parser.parseExpression(0, tokenStream, ctx, true);

				if(!tokenStream.headIs("(end)")) {
					System.out.println("\nMultiple expressions on line");
				}

				System.out.println("\nParsed expression:\n" + tree);
			} catch(ParserException pex) {
				pex.printStackTrace();
			}

			System.out.print("\nEnter a command (blank line to exit): ");
			ln = scn.nextLine();
		}

		System.out.println();
		System.out.println("\nContext is: " + ctx);

		scn.close();
	}

	private static Iterator<Token<String, String>> preprocessInput(Set<String> ops, TokenSplitter split, String ln,
			Set<String> reserved, TestContext ctx) {
		String[] rawTokens = ln.split("\\s+");

		List<String> splitTokens = new LinkedList<>();

		for(String raw : rawTokens) {
			boolean doSplit = false;

			for(String op : ops) {
				if(raw.contains(op)) {
					doSplit = true;
					break;
				}
			}

			if(doSplit) {
				String[] strangs = split.split(raw);

				splitTokens.addAll(Arrays.asList(strangs));
			} else {
				splitTokens.add(raw);
			}
		}

		System.out.println("\nSplit string: " + splitTokens);

		Iterator<String> source = splitTokens.iterator();

		Iterator<Token<String, String>> tokens = new TransformIterator<>(source,
				new Tokenizer(ops, reserved, ctx));
		return tokens;
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

		parser.addNonInitialCommand("if", ternary(5, 0, "else", litToken("cond"), false));

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

		parser.addNonInitialCommand("[", postCircumfix(60, 0, "]", litToken("idx")));

		parser.addInitialCommand("if", preTernary(0, 0, 0, "then", "else", litToken("ifelse")));

		parser.addInitialCommand("(", grouping(0, ")", litToken("parens")));

		parser.addInitialCommand("begin", delimited(0, ";", "end", litToken("block"), new BlockEnter(), idfun,
				new BlockExit(), true));

		parser.addInitialCommand("[", delimited(0, ",", "]", litToken("array"), idfun, idfun, idfun, false));

		parser.addInitialCommand("{", delimited(0, ",", "}", litToken("json"), idfun, idfun, idfun, false));

		parser.addInitialCommand("case", unary(5));

		parser.addInitialCommand("-", unary(30));

		InitialCommand<String, String, TestContext> root = unary(50);
		parser.addInitialCommand("sqrt", root);
		parser.addInitialCommand("cbrt", root);

		InitialCommand<String, String, TestContext> leaf = leaf();
		parser.addInitialCommand("(literal)", leaf);
		parser.addInitialCommand("(vref)", leaf);

		parser.addInitialCommand("var", new VarCommand());

		parser.addInitialCommand("switch", new SwitchCommand());

		return parser;
	}
}
