package dk.bec.unittest.becut.integrationtests;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dk.bec.unittest.becut.compilelist.Parse;
import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import dk.bec.unittest.becut.testcase.model.ExternalCall;
import dk.bec.unittest.becut.testcase.BecutTestCaseSuiteManager;
import dk.bec.unittest.becut.testcase.model.BecutTestCaseSuite;
import junit.framework.TestCase;

public class TestCaseSerializationIT extends TestCase {

	private static ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testSerializeMAT510RS() throws Exception {
		File file = new File("./src/test/resources/compilelistings/mat510rs_compile_listing.txt");
		CompileListing compileListing = Parse.parse(file);
		
		BecutTestCaseSuite testCaseSuite = BecutTestCaseSuiteManager.createTestCaseSuiteFromCompileListing(compileListing);
		BecutTestCase testCase = testCaseSuite.get(0);
		ExternalCall externalCall = testCase.getExternalCalls().get(0);

		externalCall.addIteration();
		
		try {
			File tempSerialization = File.createTempFile("becut", ".testcase");
			tempSerialization.deleteOnExit();
			System.out.println(tempSerialization.getAbsolutePath());
			mapper.writer().writeValue(tempSerialization, testCase);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//TODO: add assertions on the json file
		System.out.println("We're done");
	}
}
