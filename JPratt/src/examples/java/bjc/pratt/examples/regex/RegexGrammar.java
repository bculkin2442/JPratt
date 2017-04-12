package bjc.pratt.examples.regex;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import bjc.utils.data.GeneratingIterator;
import bjc.utils.funcdata.IList;
import bjc.utils.parserutils.TokenUtils;
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

		final Iterator<Integer> numbers = new GeneratingIterator<>(0, (num) -> num + 1, (val) -> true);

		final Map<String, String> stringLiterals = new HashMap<>();
		final Destringer destringer = new Destringer(numbers, stringLiterals);

		final TokenSplitter dquoteSplitter = new TokenUtils.StringTokenSplitter();
		final TokenSplitter dquoteRemover = new TransformTokenSplitter(dquoteSplitter, destringer);

		final ConfigurableTokenSplitter regexSplitter = new ConfigurableTokenSplitter(true);
		regexSplitter.addSimpleDelimiters("+", "|");

		while (!ln.equals("")) {
			final IList<String> quotelessTokens = dquoteRemover.split(ln);

			System.out.println("\nTokens without quoted strings: " + quotelessTokens);

			System.out.print("\nEnter text to parse (blank line to exit): ");
			ln = scn.nextLine().trim();
		}

		System.out.println("\nString table: ");
		for (final Entry<String, String> entry : stringLiterals.entrySet()) {
			System.out.printf("\t%s\t'%s'\n", entry.getKey(), entry.getValue());
		}

		scn.close();
	}
}
