package bjc.pratt.examples.regex;

import java.util.Iterator;
import java.util.Map;
import java.util.function.UnaryOperator;

import bjc.utils.data.GeneratingIterator;

final class Destringer implements UnaryOperator<String> {
	private final Iterator<Integer>		numbers;
	public final Map<String, String>	stringLiterals;

	public Destringer(final Map<String, String> literals) {
		numbers = new GeneratingIterator<>(0, (num) -> num + 1, (val) -> true);
		stringLiterals = literals;
	}

	@Override
	public String apply(final String token) {
		if (token.startsWith("\"") && token.endsWith("\"")) {
			final String symName = "stringLiteral" + Integer.toString(numbers.next());

			final String dequotedString = token.substring(1, token.length() - 1);
			stringLiterals.put(symName, dequotedString);

			return symName;
		}

		return token;
	}
}