package dk.bec.unittest.becut;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import dk.bec.unittest.becut.Tuples.Tuple2;

public class TuplesTest {
	@Test
	public void test1() {
		Tuple2<Integer, String> key1 = Tuples.of(1, "frokost"); 
		Tuple2<Integer, String> key2 = Tuples.of(1, "frokost");
		assertTrue(key1.equals(key2));
	}
	
	@Test
	public void test2() {
		Map<Tuple2<Integer, String>, String> map = new HashMap<>();
		Tuple2<Integer, String> key = Tuples.of(1, "frokost"); 
		map.put(key, "lunch");
		assertEquals(map.get(key), "lunch");
	}
}
