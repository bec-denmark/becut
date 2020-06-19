package dk.bec.unittest.becut.integrationtests;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;

import dk.bec.unittest.becut.compilelist.Parse;
import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.testcase.BecutTestCaseManager;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import dk.bec.unittest.becut.testcase.model.ExternalCall;
import junit.framework.TestCase;

public class TestCaseSerializationIT extends TestCase {

	@Test
	public void testSerializeMAT510RS() {
		File file = new File("./src/test/resources/compilelistings/mat510rs_compile_listing.txt");
		try {
			CompileListing compileListing = Parse.parse(file);
			
			BecutTestCase testCase = BecutTestCaseManager.createTestCaseFromCompileListing(compileListing);			
			ExternalCall externalCall = testCase.getExternalCalls().get(0);
			
			
			System.out.println("We're done");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}
}
