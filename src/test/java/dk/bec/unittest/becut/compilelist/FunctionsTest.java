package dk.bec.unittest.becut.compilelist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import dk.bec.unittest.becut.compilelist.model.CompileListing;
import koopa.core.trees.Tree;

public class FunctionsTest {
	@Test
	public void shouldReturnTrueForDB2Program() throws Exception {
		File file = new File("./src/test/resources/compilelistings/RDZDB2.txt");
		CompileListing compileListing = Parse.parse(file);
		Tree ast = compileListing.getSourceMapAndCrossReference().getAst();
		assertTrue(Functions.hasDB2Calls(ast));
	}

	@Test
	public void shouldReturnFalseForNotDB2Program() throws Exception {
		//TODO
	}
	
	@Test
	public void shouldStripDoubleQuotes() throws Exception {
		assertEquals("aa", Functions.stripQuotes("\"aa\""));
	}

	@Test
	public void shouldStripSingleQuotes() throws Exception {
		assertEquals("aa", Functions.stripQuotes("'aa'"));
	}

	@Test
	public void shouldReturnedUnchanged() throws Exception {
		assertEquals("aa", Functions.stripQuotes("aa"));
	}

	@Test
	public void shouldReturnOneChar() throws Exception {
		assertEquals("a", Functions.stripQuotes("'a'"));
	}
}
