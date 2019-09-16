package dk.bec.unittest.becut.compilelist;

import java.io.File;
import java.io.FileNotFoundException;

import dk.bec.unittest.becut.compilelist.Parse;
import dk.bec.unittest.becut.compilelist.model.CompileListing;
import junit.framework.TestCase;

public class ParseTest extends TestCase {
	
	public void testCreateCompileListMAT510RS() {
		File file = new File("./src/test/resources/compilelistings/mat510rs_compile_listing.txt");
		try {
			CompileListing compileListing = Parse.parse(file);
			assertNotNullCompileListing(compileListing);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}

	public void testCreateCompileListMAT512RS() {
		File file = new File("./src/test/resources/compilelistings/mat512rs_compile_listing.txt");
		try {
			CompileListing compileListing = Parse.parse(file);
			assertNotNullCompileListing(compileListing);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}
	
	public void testCreateCompileListSyntaxCheck() {
		File file = new File("./src/test/resources/compilelistings/mat514rs_syntaxcheck_listing.txt");
		try {
			CompileListing compileListing = Parse.parse(file);
			assertNotNullCompileListing(compileListing);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}
	
	private void assertNotNullCompileListing(CompileListing compileListing) {
		assertNotNull(compileListing);
		assertNotNull(compileListing.getCompileOptions());
		assertNotNull(compileListing.getDataDivisionMap());
		assertNotNull(compileListing.getDataNamesCrossReference());
		assertNotNull(compileListing.getInvocationParameters());
		assertNotNull(compileListing.getOriginalSource());
		assertNotNull(compileListing.getProceduresCrossReference());
		assertNotNull(compileListing.getProgramsCrossReference());
		assertNotNull(compileListing.getSourceMapAndCrossReference());
	}

}
