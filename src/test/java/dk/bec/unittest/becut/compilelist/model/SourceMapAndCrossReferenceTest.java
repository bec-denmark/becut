package dk.bec.unittest.becut.compilelist.model;

import java.io.File;
import java.io.FileNotFoundException;

import dk.bec.unittest.becut.compilelist.CobolNodeType;
import dk.bec.unittest.becut.compilelist.Parse;
import dk.bec.unittest.becut.compilelist.TreeUtil;
import junit.framework.TestCase;
import koopa.core.trees.Tree;

public class SourceMapAndCrossReferenceTest extends TestCase {
	
	public void testSourceMAT510RS() {
		File file = new File("./src/test/resources/compilelistings/mat510rs_compile_listing.txt");
		try {
			CompileListing compileListing = Parse.parse(file);
			SourceMapAndCrossReference source = compileListing.getSourceMapAndCrossReference();

			Tree ast = source.getAst();
			Tree display = TreeUtil.getDescendents(ast, CobolNodeType.DISPLAY_STATEMENT).get(1);
			assertEquals("DISPLAY 'Result of call is: ' MAT511-SUM", display.getProgramText());
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}

	public void testSourceMAT512RS() {
		File file = new File("./src/test/resources/compilelistings/mat512rs_compile_listing.txt");
		try {
			CompileListing compileListing = Parse.parse(file);
			SourceMapAndCrossReference source = compileListing.getSourceMapAndCrossReference();

			Tree ast = source.getAst();
			Tree display = TreeUtil.getDescendents(ast, CobolNodeType.DISPLAY_STATEMENT).get(0);
			assertEquals("DISPLAY 'Main program MAT512 WAS CALLED'", display.getProgramText());
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}

	public void testSourceMAT514RS() {
		File file = new File("./src/test/resources/compilelistings/mat514rs_syntaxcheck_listing.txt");
		try {
			CompileListing compileListing = Parse.parse(file);
			SourceMapAndCrossReference source = compileListing.getSourceMapAndCrossReference();

			Tree ast = source.getAst();
			Tree display = TreeUtil.getDescendents(ast, CobolNodeType.DATA_DESCRIPTION_ENTRY).get(0);
			assertEquals("01  MAT514-AREA.", display.getProgramText());
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}

}
