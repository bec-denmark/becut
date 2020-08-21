package dk.bec.unittest.becut.integrationtests;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;

import dk.bec.unittest.becut.compilelist.Parse;
import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.testcase.BecutTestCaseSuiteManager;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import dk.bec.unittest.becut.testcase.model.BecutTestCaseSuite;
import junit.framework.TestCase;

public class BecutTestCaseManagerIT extends TestCase {
	public void testGenerateTestScriptMAT510RS() throws FileNotFoundException {
		File file = new File("./src/test/resources/compilelistings/mat510rs_compile_listing.txt");
		CompileListing compileListing = Parse.parse(file);
		
		BecutTestCaseSuite testCaseSuite = BecutTestCaseSuiteManager.createTestCaseSuiteFromCompileListing(compileListing);			
		BecutTestCase testCase = testCaseSuite.get(0);
		
		assertThat(testCase.getExternalCalls(), hasSize(1));
		assertThat(testCase.getExternalCalls().get(0).getIterations().get("iteration_0").getParameters(), hasSize(1));
		assertThat(testCase.getExternalCalls().get(0).getIterations().get("iteration_0").getParameters().get(0).getSubStructure(), hasSize(3));
		assertThat(testCase.getExternalCalls().get(0).getIterations().get("iteration_0").getParameters().get(0).getSubStructure().get(2).getSubStructure(), hasSize(3));
		assertThat(testCase.getExternalCalls().get(0).getIterations().get("iteration_0").getParameters().get(0).getSubStructure().get(2).getSubStructure().get(0).getSubStructure(), hasSize(2));
		assertThat(testCase.getExternalCalls().get(0).getIterations().get("iteration_0").getParameters().get(0).getSubStructure().get(2).getSubStructure().get(0).getSubStructure().get(0).getSubStructure(), hasSize(0));
	}
}
