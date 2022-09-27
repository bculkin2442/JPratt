package bjc.test.pratt.tokens;

import static org.junit.Assert.*;
import static bjc.test.pratt.TestUtils.*;

import java.util.Iterator;

import org.junit.Test;

import bjc.data.ArrayIterator;
import bjc.pratt.tokens.StringTokenStream;
import bjc.pratt.tokens.Token;

@SuppressWarnings("javadoc")
public class StringTokenStreamTest {

	@Test
	public void test() {
		Token<String, String> tokenA = token("a");
		Token<String, String> tokenB = token("b");
		Token<String, String> tokenC = token("c");
		Token<String, String> tokenD = token("d");
		
		Iterator<Token<String, String>> itr = new ArrayIterator<>(tokenA, tokenB, tokenC, tokenD);
		StringTokenStream strm = new StringTokenStream(itr);
		
		assertNull(strm.current());
		assertEquals(tokenA, strm.next());
		assertEquals(tokenB, strm.next());
		strm.mark();
		assertEquals(tokenC, strm.next());
		assertEquals(tokenD, strm.next());
		strm.rollback();
		assertEquals(tokenC, strm.next());
		assertEquals(tokenD, strm.next());
		assertEquals(token("(end)"), strm.next());
	}

}
