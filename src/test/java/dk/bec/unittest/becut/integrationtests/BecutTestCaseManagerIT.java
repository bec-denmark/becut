package dk.bec.unittest.becut.integrationtests;

import java.io.File;
import java.io.FileNotFoundException;

import dk.bec.unittest.becut.compilelist.Parse;
import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.testcase.BecutTestCaseManager;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import junit.framework.TestCase;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

public class BecutTestCaseManagerIT extends TestCase {

	public void testGenerateTestScriptMAT510RS() {
		File file = new File("./src/test/resources/compilelistings/mat510rs_compile_listing.txt");
		try {
			CompileListing compileListing = Parse.parse(file);
			
			BecutTestCase testCase = BecutTestCaseManager.createTestCaseFromCompileListing(compileListing);			
			
			assertThat(testCase.getExternalCalls(), hasSize(1));
			assertThat(testCase.getExternalCalls().get(0).getIterations().get("iteration_0").getParameters(), hasSize(1));
			assertThat(testCase.getExternalCalls().get(0).getIterations().get("iteration_0").getParameters().get(0).getSubStructure(), hasSize(3));
			assertThat(testCase.getExternalCalls().get(0).getIterations().get("iteration_0").getParameters().get(0).getSubStructure().get(2).getSubStructure(), hasSize(3));
			assertThat(testCase.getExternalCalls().get(0).getIterations().get("iteration_0").getParameters().get(0).getSubStructure().get(2).getSubStructure().get(0).getSubStructure(), hasSize(2));
			assertThat(testCase.getExternalCalls().get(0).getIterations().get("iteration_0").getParameters().get(0).getSubStructure().get(2).getSubStructure().get(0).getSubStructure().get(0).getSubStructure(), hasSize(0));

			

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}


}
