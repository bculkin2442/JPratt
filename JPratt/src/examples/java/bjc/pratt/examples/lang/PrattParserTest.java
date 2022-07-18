package bjc.pratt.examples.lang;

import static bjc.pratt.commands.impls.InitialCommands.delimited;
import static bjc.pratt.commands.impls.InitialCommands.grouping;
import static bjc.pratt.commands.impls.InitialCommands.leaf;
import static bjc.pratt.commands.impls.InitialCommands.preTernary;
import static bjc.pratt.commands.impls.InitialCommands.unary;
import static bjc.pratt.commands.impls.NonInitialCommands.chain;
import static bjc.pratt.commands.impls.NonInitialCommands.infixLeft;
import static bjc.pratt.commands.impls.NonInitialCommands.infixNon;
import static bjc.pratt.commands.impls.NonInitialCommands.infixRight;
import static bjc.pratt.commands.impls.NonInitialCommands.postCircumfix;
import static bjc.pratt.commands.impls.NonInitialCommands.postfix;
import static bjc.pratt.commands.impls.NonInitialCommands.ternary;
import static bjc.pratt.tokens.StringToken.litToken;
import static bjc.functypes.ID.id;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.UnaryOperator;

import bjc.pratt.PrattParser;
import bjc.pratt.commands.InitialCommand;
import bjc.pratt.commands.NonInitialCommand;
import bjc.pratt.tokens.StringToken;
import bjc.pratt.tokens.StringTokenStream;
import bjc.pratt.tokens.Token;
import bjc.data.Tree;
import bjc.data.TransformIterator;
import bjc.funcdata.ListEx;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.splitter.ConfigurableTokenSplitter;
import bjc.utils.parserutils.splitter.ExcludingTokenSplitter;
import bjc.utils.parserutils.splitter.FilteredTokenSplitter;
import bjc.utils.parserutils.splitter.TokenSplitter;

/**
 * Simple test for Pratt parser.
 *
 * @author EVE
 *
 */
public class PrattParserTest {
	private final static Set<String> ops;
	private final static Set<String> reserved;
	
	static {
		/*
		 * Setup operator hash.
		 * 
		 * Use a linked hash set to preserve insertion order.
		 */
		ops = new LinkedHashSet<>();

		ops.add("!!!");

		ops.add(":=");
		
		ops.addAll(Arrays.asList("|>", "[|]"));
		
		ops.addAll(Arrays.asList("->", "=>"));
		ops.addAll(Arrays.asList("||", "&&"));
		ops.addAll(Arrays.asList("<=", ">="));

		ops.addAll(Arrays.asList("\u00B1")); // Unicode plus/minus
		ops.addAll(Arrays.asList(".", ",", ";", ":"));
		ops.addAll(Arrays.asList("=", "<", ">"));
		ops.addAll(Arrays.asList("+", "-", "*", "/"));
		ops.addAll(Arrays.asList("^", "!"));
		ops.addAll(Arrays.asList("(", ")"));
		ops.addAll(Arrays.asList("[", "]"));
		ops.addAll(Arrays.asList("{", "}"));
		
		reserved = new LinkedHashSet<>();
		reserved.addAll(Arrays.asList("if", "then", "else"));
		reserved.addAll(Arrays.asList("and", "or"));
		reserved.addAll(Arrays.asList("begin", "end"));
		reserved.addAll(Arrays.asList("switch", "case"));
		reserved.addAll(Arrays.asList("sqrt", "cbrt", "root"));
		reserved.addAll(Arrays.asList("try", "throw", "catch", "finally"));
		reserved.add("var");
	}
	/**
	 * Main method.
	 *
	 * @param args
	 *            Unused CLI arguments.
	 */
	public static void main(final String[] args) {
		final ConfigurableTokenSplitter lo = new ConfigurableTokenSplitter(true);

		lo.addSimpleDelimiters(":=");
		
		lo.addSimpleDelimiters("|>", "[|]");
		
		lo.addSimpleDelimiters("->, =>");
		lo.addSimpleDelimiters("||", "&&");
		lo.addSimpleDelimiters("<=", ">=");
		
		lo.addSimpleDelimiters("\u00B1"); // Unicode plus/minus
		lo.addSimpleDelimiters(".", ",", ";", ":");
		lo.addSimpleDelimiters("=", "<", ">");
		lo.addSimpleDelimiters("+", "-", "*", "/");
		lo.addSimpleDelimiters("^");

		lo.addMultiDelimiters("!");
		lo.addMultiDelimiters("(", ")");
		lo.addMultiDelimiters("[", "]");
		lo.addMultiDelimiters("{", "}");

		lo.compile();

		final ExcludingTokenSplitter excluder = new ExcludingTokenSplitter(lo);

		excluder.addLiteralExclusions(reserved.toArray(new String[0]));

		final FilteredTokenSplitter filtered = new FilteredTokenSplitter(excluder, (tok) -> !tok.equals(""));

		final PrattParser<String, String, TestContext> parser = createParser();

		final TestContext ctx = new TestContext();

		final Scanner scn = new Scanner(System.in);

		System.out.print("Enter a command (blank line to exit): ");
		String ln = scn.nextLine();

		while (!ln.trim().equals("")) {
			final Iterator<Token<String, String>> tokens = preprocessInput(ops, reserved, filtered, ln, ctx);

			try {
				final StringTokenStream tokenStream = new StringTokenStream(tokens);

				/*
				 * Prime stream.
				 */
				tokenStream.next();

				final Tree<Token<String, String>> rawTree = parser.parseExpression(0, tokenStream, ctx, true);

				if (!tokenStream.headIs("(end)")) {
					System.out.println("\nMultiple expressions on line");
				}

				System.out.printf("\nParsed expression:\n%s", rawTree);
			} catch (ParserException pex) {
				pex.printStackTrace();
			}

			System.out.print("\nEnter a command (blank line to exit): ");
			ln = scn.nextLine();
		}

		System.out.println();
		System.out.printf("\nContext is: %s\n", ctx);

		scn.close();
	}

	private static Iterator<Token<String, String>> preprocessInput(final Set<String> oops, final Set<String> reservd,
			final TokenSplitter split, final String ln, final TestContext ctx) {
		final String[] rawTokens = ln.split("\\r\\.\\r");

		final List<String> splitTokens = new LinkedList<>();

		for (final String raw : rawTokens) {
			if (raw.equals(""))
				continue;

			boolean doSplit = false;

			for (final String op : oops) {
				if (raw.contains(op)) {
					doSplit = true;
					break;
				}
			}

			String strang = raw.replaceAll("\\.(\\.+)", "$1");
			
			if (doSplit) {	
				ListEx<String> splitStrangs = split.split(strang);
				splitStrangs.removeMatching("");

				splitStrangs.forEach(splitTokens::add);
			} else {
				splitTokens.add(strang);
			}
		}

		System.out.println("\nSplit string: " + splitTokens);

		final Iterator<String> source = splitTokens.iterator();

		final Tokenizer tokenzer = new Tokenizer(oops, reservd, ctx);

		final Iterator<Token<String, String>> tokens = new TransformIterator<>(source, tokenzer);

		return tokens;
	}

	private static PrattParser<String, String, TestContext> createParser() {
		/*
		 * Set of which relational operators chain with each other.
		 */
		final HashSet<String> relChain = new HashSet<>();
		relChain.addAll(Arrays.asList("=", "<", ">", "<=", ">="));

		/*
		 * Token for marking chains.
		 */
		final StringToken chainToken = litToken("and");

		/*
		 * ID function.
		 */
		final UnaryOperator<TestContext> idfun = id();

		final PrattParser<String, String, TestContext> parser = new PrattParser<>();

		/*
		 * Statement ender.
		 */
		parser.addNonInitialCommand("!!!", postfix(0));

		/*
		 * Separator.
		 */
		parser.addNonInitialCommand(":", infixNon(3));

		/*
		 * Finally block.
		 */
		parser.addNonInitialCommand("finally", infixLeft(4));

		/*
		 * Catch block.
		 */
		parser.addNonInitialCommand("catch", infixLeft(5));

		/*
		 * Inline conditional.
		 */
		final NonInitialCommand<String, String, TestContext> ifElse = ternary(6, 0, "else", litToken("cond"), false);
		parser.addNonInitialCommand("if", ifElse);

		/*
		 * Assignment.
		 */
		parser.addNonInitialCommand(":=", new AssignCommand(10));

		/*
		 * Lambda definer.
		 */
		parser.addNonInitialCommand("->", infixRight(11));

		/*
		 * Pipeline operator.
		 */
		parser.addNonInitialCommand("|>", infixLeft(12));
		
		/*
		 * Non-short circuiting conditionals.
		 */
		final NonInitialCommand<String, String, TestContext> nonSSRelJoin = infixLeft(13);
		parser.addNonInitialCommand("and", nonSSRelJoin);
		parser.addNonInitialCommand("or", nonSSRelJoin);

		/*
		 * Relational operators.
		 */
		final NonInitialCommand<String, String, TestContext> chainRelOp = chain(15, relChain, chainToken);
		parser.addNonInitialCommand("=", chainRelOp);
		parser.addNonInitialCommand("<", chainRelOp);
		parser.addNonInitialCommand(">", chainRelOp);
		parser.addNonInitialCommand("<=", chainRelOp);
		parser.addNonInitialCommand(">=", chainRelOp);

		/*
		 * Short-circuiting conditionals.
		 */
		final NonInitialCommand<String, String, TestContext> ssRelJoin = infixRight(17);
		parser.addNonInitialCommand("&&", ssRelJoin);
		parser.addNonInitialCommand("||", ssRelJoin);

		/*
		 * Range operator.
		 */
		parser.addNonInitialCommand("[|]", infixNon(18));
		
		/*
		 * Add/subtracting operators.
		 */
		final NonInitialCommand<String, String, TestContext> addSub = infixLeft(20);
		parser.addNonInitialCommand("+", addSub);
		parser.addNonInitialCommand("-", addSub);
		parser.addNonInitialCommand("\u00B1", addSub); // Unicode plus/minus

		/*
		 * Multiply/divide operators.
		 */
		final NonInitialCommand<String, String, TestContext> mulDiv = infixLeft(30);
		parser.addNonInitialCommand("*", mulDiv);
		parser.addNonInitialCommand("/", mulDiv);

		/*
		 * Conditional negation.
		 */
		parser.addNonInitialCommand("!", postfix(40));

		/*
		 * Exponentiation.
		 */
		final NonInitialCommand<String, String, TestContext> expon = infixRight(50);
		final NonInitialCommand<String, String, TestContext> superexpon = postfix(50);
		parser.addNonInitialCommand("^", expon);
		parser.addNonInitialCommand("root", expon);
		parser.addNonInitialCommand("(superexp)", superexpon);

		/*
		 * Member access.
		 */
		parser.addNonInitialCommand(".", infixLeft(60));

		/*
		 * Array indexing.
		 */
		final NonInitialCommand<String, String, TestContext> arrayIdx = postCircumfix(60, 0, "]", litToken("idx"));
		parser.addNonInitialCommand("[", arrayIdx);

		/*
		 * Statement conditional.
		 */
		final InitialCommand<String, String, TestContext> ifThenElse = preTernary(0, 0, 0, "then", "else",
				litToken("ifelse"));
		parser.addInitialCommand("if", ifThenElse);

		/*
		 * Grouping parens.
		 */
		final InitialCommand<String, String, TestContext> parens = grouping(0, ")", litToken("parens"));
		parser.addInitialCommand("(", parens);

		/*
		 * Blocks.
		 */
		final InitialCommand<String, String, TestContext> scoper = delimited(0, ";", "end", litToken("block"),
				new BlockEnter(), idfun, new BlockExit(), true);
		parser.addInitialCommand("begin", scoper);

		/*
		 * Array literals.
		 */
		final InitialCommand<String, String, TestContext> arrayLiteral = delimited(0, ",", "]", litToken("array"),
				idfun, idfun, idfun, false);
		parser.addInitialCommand("[", arrayLiteral);

		/*
		 * JSON literals.
		 */
		final InitialCommand<String, String, TestContext> jsonLiteral = delimited(0, ",", "}", litToken("json"), idfun,
				idfun, idfun, false);
		parser.addInitialCommand("{", jsonLiteral);

		/*
		 * Try block.
		 */
		parser.addInitialCommand("try", unary(3));

		/*
		 * Case block.
		 */
		parser.addInitialCommand("case", unary(5));

		/*
		 * Throw statement.
		 */
		parser.addInitialCommand("throw", unary(10));

		/*
		 * Negation.
		 */
		parser.addInitialCommand("-", unary(30));

		/*
		 * Roots.
		 */
		final InitialCommand<String, String, TestContext> root = unary(50);
		parser.addInitialCommand("sqrt", root);
		parser.addInitialCommand("cbrt", root);

		/*
		 * Literals.
		 */
		final InitialCommand<String, String, TestContext> leaf = leaf();
		parser.addInitialCommand("(literal)", leaf);

		/*
		 * Variable declaration.
		 */
		parser.addInitialCommand("var", new VarCommand());

		/*
		 * Switch statement.
		 */
		parser.addInitialCommand("switch", new SwitchCommand());

		return parser;
	}
}
