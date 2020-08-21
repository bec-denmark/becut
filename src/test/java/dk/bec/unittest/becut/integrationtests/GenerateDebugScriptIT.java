package dk.bec.unittest.becut.integrationtests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;

import dk.bec.unittest.becut.compilelist.Parse;
import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.debugscript.ScriptGenerator;
import dk.bec.unittest.becut.debugscript.model.DebugScript;
import dk.bec.unittest.becut.testcase.BecutTestCaseSuiteManager;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import dk.bec.unittest.becut.testcase.model.BecutTestCaseSuite;

public class GenerateDebugScriptIT {
	@Ignore
	@Test
	public void testCreateDebugScriptMAT510RS() throws Exception {
		File file = new File("./src/test/resources/compilelistings/mat510rs_compile_listing.txt");
		File testScriptFile = new File("./src/test/resources/testcases/mat510rs_testcase.json");
		BecutTestCaseSuite testCaseSuite = BecutTestCaseSuiteManager.loadTestCaseSuite(testScriptFile.toPath());
		BecutTestCase testCase = testCaseSuite.get(0); 
		
		DebugScript debugScript = ScriptGenerator.generateDebugScript(testCase);
		String actualScript =debugScript.generate();
		String expectedScript = new String(Files.readAllBytes(Paths.get("./src/test/resources/debugscripts/mat510rs_debugscript.txt")), StandardCharsets.UTF_8);

		assertEquals(expectedScript, actualScript);
	}
	
	@Ignore
	@Test
	public void testCreateDebugScriptMAT512RS() throws Exception {
		File file = new File("./src/test/resources/compilelistings/mat512rs_compile_listing.txt");
		File testScriptFile = new File("./src/test/resources/testcases/mat512rs_testcase.json");
		CompileListing compileListing = Parse.parse(file);
		BecutTestCaseSuite testCaseSuite = BecutTestCaseSuiteManager.loadTestCaseSuite(testScriptFile.toPath());
		BecutTestCase testCase = testCaseSuite.get(0); 
		
		DebugScript debugScript = ScriptGenerator.generateDebugScript(testCase);
		String actualScript =debugScript.generate();
		String expectedScript = new String(Files.readAllBytes(Paths.get("./src/test/resources/debugscripts/mat512rs_debugscript.txt")), StandardCharsets.UTF_8);

		assertEquals(expectedScript, actualScript);
	}

	@Ignore
	@Test
	public void testCreateDebugScriptMAT561() throws Exception {
		File file = new File("./src/test/resources/compilelistings/mat561_compile_listing.txt");
		File testScriptFile = new File("./src/test/resources/testcases/mat561_testcase.json");
		CompileListing compileListing = Parse.parse(file);
		BecutTestCaseSuite testCaseSuite = BecutTestCaseSuiteManager.loadTestCaseSuite(testScriptFile.toPath());
		BecutTestCase testCase = testCaseSuite.get(0); 
		//testCase.setCompileListing(compileListing);
		
		DebugScript debugScript = ScriptGenerator.generateDebugScript(testCase);
		String actualScript = debugScript.generate();
		System.out.println(actualScript);
//		String expectedScript = new String(Files.readAllBytes(Paths.get("./src/test/resources/debugscripts/mat512rs_debugscript.txt")), StandardCharsets.UTF_8);
//
//		assertEquals(expectedScript, actualScript);
	}
}
