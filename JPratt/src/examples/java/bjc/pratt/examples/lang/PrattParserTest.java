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
import static bjc.utils.functypes.ID.id;

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
import bjc.utils.data.ITree;
import bjc.utils.data.TransformIterator;
import bjc.utils.funcdata.IList;
import bjc.utils.parserutils.ParserException;
import bjc.utils.parserutils.splitter.ChainTokenSplitter;
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
	/**
	 * Main method.
	 *
	 * @param args
	 *            Unused CLI arguments.
	 */
	public static void main(final String[] args) {
		/*
		 * Use a linked hash set to preserve insertion order.
		 */
		final Set<String> ops = new LinkedHashSet<>();

		ops.add("!!!");

		ops.addAll(Arrays.asList("->", "=>"));
		ops.add(":=");
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

		/*
		 * Reserved words that represent themselves, not literals.
		 */
		final Set<String> reserved = new LinkedHashSet<>();
		reserved.addAll(Arrays.asList("if", "then", "else"));
		reserved.addAll(Arrays.asList("and", "or"));
		reserved.addAll(Arrays.asList("begin", "end"));
		reserved.addAll(Arrays.asList("switch", "case"));
		reserved.addAll(Arrays.asList("sqrt", "cbrt", "root"));
		reserved.addAll(Arrays.asList("try", "catch", "finally"));
		reserved.add("var");

		final ConfigurableTokenSplitter lo = new ConfigurableTokenSplitter(true);

		lo.addSimpleDelimiters("->");
		lo.addSimpleDelimiters(":=");
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
			final Iterator<Token<String, String>> tokens = preprocessInput(ops, filtered, ln, reserved, ctx);

			try {
				final StringTokenStream tokenStream = new StringTokenStream(tokens);

				/*
				 * Prime stream.
				 */
				tokenStream.next();

				final ITree<Token<String, String>> tree = parser.parseExpression(0, tokenStream, ctx, true);

				if (!tokenStream.headIs("(end)")) {
					System.out.println("\nMultiple expressions on line");
				}

				System.out.println("\nParsed expression:\n" + tree);
			} catch (final ParserException pex) {
				pex.printStackTrace();
			}

			System.out.print("\nEnter a command (blank line to exit): ");
			ln = scn.nextLine();
		}

		System.out.println();
		System.out.println("\nContext is: " + ctx);

		scn.close();
	}

	private static Iterator<Token<String, String>> preprocessInput(final Set<String> ops, final TokenSplitter split,
			final String ln, final Set<String> reserved, final TestContext ctx) {
		final String[] rawTokens = ln.split("\\s+");

		final List<String> splitTokens = new LinkedList<>();

		for (final String raw : rawTokens) {
			if (raw.equals(""))
				continue;

			boolean doSplit = false;

			for (final String op : ops) {
				if (raw.contains(op)) {
					doSplit = true;
					break;
				}
			}

			if (doSplit) {
				IList<String> splitStrangs = split.split(raw);
				splitStrangs.removeMatching("");

				splitStrangs.forEach(splitTokens::add);
			} else {
				splitTokens.add(raw);
			}
		}

		System.out.println("\nSplit string: " + splitTokens);

		final Iterator<String> source = splitTokens.iterator();

		final Tokenizer tokenzer = new Tokenizer(ops, reserved, ctx);

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

		parser.addNonInitialCommand("!!!", postfix(0));

		parser.addNonInitialCommand(":", infixNon(3));

		parser.addNonInitialCommand("finally", infixLeft(4));

		parser.addNonInitialCommand("catch", infixLeft(5));

		final NonInitialCommand<String, String, TestContext> ifElse = ternary(6, 0, "else", litToken("cond"), false);
		parser.addNonInitialCommand("if", ifElse);

		parser.addNonInitialCommand(":=", new AssignCommand(10));

		parser.addNonInitialCommand("->", infixRight(11));

		final NonInitialCommand<String, String, TestContext> nonSSRelJoin = infixLeft(13);
		parser.addNonInitialCommand("and", nonSSRelJoin);
		parser.addNonInitialCommand("or", nonSSRelJoin);

		final NonInitialCommand<String, String, TestContext> chainRelOp = chain(15, relChain, chainToken);
		parser.addNonInitialCommand("=", chainRelOp);
		parser.addNonInitialCommand("<", chainRelOp);
		parser.addNonInitialCommand(">", chainRelOp);
		parser.addNonInitialCommand("<=", chainRelOp);
		parser.addNonInitialCommand(">=", chainRelOp);

		final NonInitialCommand<String, String, TestContext> ssRelJoin = infixRight(17);
		parser.addNonInitialCommand("&&", ssRelJoin);
		parser.addNonInitialCommand("||", ssRelJoin);

		final NonInitialCommand<String, String, TestContext> addSub = infixLeft(20);
		parser.addNonInitialCommand("+", addSub);
		parser.addNonInitialCommand("-", addSub);
		parser.addNonInitialCommand("\u00B1", addSub); // Unicode plus/minus

		final NonInitialCommand<String, String, TestContext> mulDiv = infixLeft(30);
		parser.addNonInitialCommand("*", mulDiv);
		parser.addNonInitialCommand("/", mulDiv);

		parser.addNonInitialCommand("!", postfix(40));

		final NonInitialCommand<String, String, TestContext> expon = infixRight(50);
		parser.addNonInitialCommand("^", expon);
		parser.addNonInitialCommand("root", expon);

		final NonInitialCommand<String, String, TestContext> superexpon = postfix(50);
		parser.addNonInitialCommand("(superexp)", superexpon);

		parser.addNonInitialCommand(".", infixLeft(60));

		final NonInitialCommand<String, String, TestContext> arrayIdx = postCircumfix(60, 0, "]", litToken("idx"));
		parser.addNonInitialCommand("[", arrayIdx);

		final InitialCommand<String, String, TestContext> ifThenElse = preTernary(0, 0, 0, "then", "else",
				litToken("ifelse"));
		parser.addInitialCommand("if", ifThenElse);

		final InitialCommand<String, String, TestContext> parens = grouping(0, ")", litToken("parens"));
		parser.addInitialCommand("(", parens);

		final InitialCommand<String, String, TestContext> scoper = delimited(0, ";", "end", litToken("block"),
				new BlockEnter(), idfun, new BlockExit(), true);
		parser.addInitialCommand("begin", scoper);

		final InitialCommand<String, String, TestContext> arrayLiteral = delimited(0, ",", "]", litToken("array"),
				idfun, idfun, idfun, false);
		parser.addInitialCommand("[", arrayLiteral);

		final InitialCommand<String, String, TestContext> jsonLiteral = delimited(0, ",", "}", litToken("json"), idfun,
				idfun, idfun, false);
		parser.addInitialCommand("{", jsonLiteral);

		parser.addInitialCommand("try", unary(3));

		parser.addInitialCommand("case", unary(5));

		parser.addInitialCommand("-", unary(30));

		final InitialCommand<String, String, TestContext> root = unary(50);
		parser.addInitialCommand("sqrt", root);
		parser.addInitialCommand("cbrt", root);

		final InitialCommand<String, String, TestContext> leaf = leaf();
		parser.addInitialCommand("(literal)", leaf);

		parser.addInitialCommand("var", new VarCommand());

		parser.addInitialCommand("switch", new SwitchCommand());

		return parser;
	}
}