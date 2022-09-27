package bjc.test.pratt;

import bjc.pratt.tokens.StringToken;
import bjc.pratt.tokens.Token;

public class TestUtils {

	public static Token<String, String> token(String val) {
		return token(val, val);
	}
	
	public static Token<String, String> token(String key, String val) {
		return new StringToken(key, val);
	}
}
