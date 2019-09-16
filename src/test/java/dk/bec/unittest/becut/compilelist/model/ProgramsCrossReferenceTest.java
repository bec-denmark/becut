package dk.bec.unittest.becut.compilelist.model;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.FileNotFoundException;

import dk.bec.unittest.becut.compilelist.Parse;
import junit.framework.TestCase;

public class ProgramsCrossReferenceTest extends TestCase {

	public void testSourceMAT510RS() {
		File file = new File("./src/test/resources/compilelistings/mat510rs_compile_listing.txt");
		try {
			CompileListing compileListing = Parse.parse(file);
			ProgramsCrossReference programsCrossReference = compileListing.getProgramsCrossReference();
			assertThat(programsCrossReference.getProgramReferences(), hasSize(0));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}

	public void testSourceMAT512RS() {
		File file = new File("./src/test/resources/compilelistings/mat512rs_compile_listing.txt");
		try {
			CompileListing compileListing = Parse.parse(file);
			ProgramsCrossReference programsCrossReference = compileListing.getProgramsCrossReference();
			assertThat(programsCrossReference.getProgramReferences(), hasSize(3));
			assertThat(programsCrossReference.getProgramReferences().get(0).getReferences(), containsInAnyOrder(214, 218, 223));
			assertThat(programsCrossReference.getProgramReferences().get(1).getProgramName(), is("DSNHADD2"));
			assertThat(programsCrossReference.getProgramReferences().get(2).getProgramType(), is("EXTERNAL"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}

	public void testSourceMAT514RS() {
		File file = new File("./src/test/resources/compilelistings/mat514rs_syntaxcheck_listing.txt");
		try {
			CompileListing compileListing = Parse.parse(file);
			ProgramsCrossReference programsCrossReference = compileListing.getProgramsCrossReference();
			assertThat(programsCrossReference.getProgramReferences(), hasSize(0));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}

}
