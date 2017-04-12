package bjc.pratt.examples.regex;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import bjc.utils.funcdata.IList;
import bjc.utils.parserutils.TokenUtils.StringTokenSplitter;
import bjc.utils.parserutils.splitter.ChainTokenSplitter;
import bjc.utils.parserutils.splitter.ConfigurableTokenSplitter;
import bjc.utils.parserutils.splitter.TokenSplitter;
import bjc.utils.parserutils.splitter.TransformTokenSplitter;

/**
 * Grammar test for regular expressions.
 *
 * @author bjculkin
 *
 */
public class RegexGrammar {
	/**
	 * Main method.
	 *
	 * @param args
	 *                Unused CLI arguments.
	 */
	public static void main(final String[] args) {
		final Scanner scn = new Scanner(System.in);

		System.out.print("Enter text to parse (blank line to exit): ");
		String ln = scn.nextLine().trim();

		final Map<String, String> stringLiterals = new HashMap<>();

		/*
		 * Build the token splitter
		 */
		final ChainTokenSplitter splitter = buildSplitter(stringLiterals);

		while (!ln.equals("")) {
			final IList<String> quotelessTokens = splitter.split(ln);

			System.out.println("\nSplit tokens: " + quotelessTokens);

			System.out.print("\nEnter text to parse (blank line to exit): ");
			ln = scn.nextLine().trim();
		}

		System.out.println("\nString table: ");
		for (final Entry<String, String> entry : stringLiterals.entrySet()) {
			System.out.printf("\t%s\t'%s'\n", entry.getKey(), entry.getValue());
		}

		scn.close();
	}

	private static ChainTokenSplitter buildSplitter(final Map<String, String> stringLiterals) {
		final Destringer destringer = new Destringer(stringLiterals);

		final TokenSplitter dquoteSplitter = new StringTokenSplitter();
		final TokenSplitter dquoteRemover = new TransformTokenSplitter(dquoteSplitter, destringer);

		final ConfigurableTokenSplitter regexSplitter = new ConfigurableTokenSplitter(true);
		regexSplitter.addSimpleDelimiters("+", "|");
		regexSplitter.compile();

		final ChainTokenSplitter splitter = new ChainTokenSplitter();
		splitter.appendSplitters(dquoteRemover, regexSplitter);

		return splitter;
	}
}
