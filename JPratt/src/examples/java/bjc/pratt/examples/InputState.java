package bjc.pratt.examples;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import bjc.pratt.Token;
import bjc.utils.data.TransformIterator;
import bjc.utils.parserutils.splitterv2.ChainTokenSplitter;
import bjc.utils.parserutils.splitterv2.ConfigurableTokenSplitter;
import bjc.utils.parserutils.splitterv2.ExcludingTokenSplitter;
import bjc.utils.parserutils.splitterv2.TokenSplitter;

/**
 * Packaged input state for the parser.
 * 
 * @author student
 *
 */
public class InputState {
	public Set<String> ops;
	public Set<String> reserved;

	public TokenSplitter splitter;

	public TestContext ctx;

	public String ln;

	private InputState(Set<String> opps, TokenSplitter splt, String lin, Set<String> rserved, TestContext ctxt) {
		ops = opps;
		reserved = rserved;

		splitter = splt;

		ctx = ctxt;

		ln = lin;
	}

	public static InputState createState() {
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

		ChainTokenSplitter nsplit = new ChainTokenSplitter();

		ConfigurableTokenSplitter hi = new ConfigurableTokenSplitter(true);
		ConfigurableTokenSplitter lo = new ConfigurableTokenSplitter(true);

		hi.addSimpleDelimiters("->");
		hi.addSimpleDelimiters(":=");
		hi.addSimpleDelimiters("||", "&&");
		hi.addSimpleDelimiters("<=", ">=");

		lo.addSimpleDelimiters("±");
		lo.addSimpleDelimiters(".", ",", ";", ":");
		lo.addSimpleDelimiters("=", "<", ">");
		lo.addSimpleDelimiters("+", "-", "*", "/");
		lo.addSimpleDelimiters("^");

		lo.addMultiDelimiters("!");
		lo.addMultiDelimiters("(", ")");
		lo.addMultiDelimiters("[", "]");
		lo.addMultiDelimiters("{", "}");

		hi.compile();
		lo.compile();

		nsplit.appendSplitters(hi, lo);

		ExcludingTokenSplitter excluder = new ExcludingTokenSplitter(nsplit);

		excluder.addLiteralExclusions(reserved.toArray(new String[0]));

		TestContext ctx = new TestContext();

		InputState state = new InputState(ops, excluder, null, reserved, ctx);

		return state;
	}

	public Iterator<Token<String, String>> preprocessInput() {
		String[] rawTokens = ln.split("\\s+");
	
		List<String> splitTokens = new LinkedList<>();
	
		for (String raw : rawTokens) {
			boolean doSplit = false;
	
			for (String op : ops) {
				if (raw.contains(op)) {
					doSplit = true;
					break;
				}
			}
	
			if (doSplit) {
				String[] strangs = splitter.split(raw).toArray(new String[0]);
	
				splitTokens.addAll(Arrays.asList(strangs));
			} else {
				splitTokens.add(raw);
			}
		}
	
		System.out.println("\nSplit string: " + splitTokens);
	
		Iterator<String> source = splitTokens.iterator();
	
		Tokenizer tokenzer = new Tokenizer(ops, reserved, ctx);
	
		Iterator<Token<String, String>> tokens = new TransformIterator<>(source, tokenzer);
	
		return tokens;
	}
}