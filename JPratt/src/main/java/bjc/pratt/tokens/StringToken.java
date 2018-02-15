package bjc.pratt.tokens;

/**
 * Simple token implementation for strings.
 *
 * @author EVE
 *
 */
public class StringToken implements Token<String, String> {
	private final String	key;
	private final String	val;

	/**
	 * Create a new string token.
	 *
	 * @param ky
	 *        The key for the token.
	 *
	 * @param vl
	 *        The value for the token.
	 */
	public StringToken(final String ky, final String vl) {
		key = ky;
		val = vl;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getValue() {
		return val;
	}

	@Override
	public int hashCode() {
		final int prime = 31;

		int result = 1;
		result = prime * result + (key == null ? 0 : key.hashCode());
		result = prime * result + (val == null ? 0 : val.hashCode());

		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(!(obj instanceof StringToken)) return false;

		final StringToken other = (StringToken) obj;

		if(key == null) {
			if(other.key != null) return false;
		} else if(!key.equals(other.key)) return false;

		if(val == null) {
			if(other.val != null) return false;
		} else if(!val.equals(other.val)) return false;

		return true;
	}

	@Override
	public String toString() {
		return String.format("StringToken [key='%s', val='%s']", key, val);
	}

	/**
	 * Create a new literal token (has same key/value).
	 *
	 * @param val
	 *        The value for the literal token.
	 *
	 * @return A literal token with that key.
	 */
	public static StringToken litToken(final String val) {
		return new StringToken(val, val);
	}
}
