package dk.bec.unittest.becut.integrationtests;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;

import dk.bec.unittest.becut.compilelist.Parse;
import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.debugscript.ScriptGenerator;
import dk.bec.unittest.becut.debugscript.model.DebugScript;
import dk.bec.unittest.becut.testcase.BecutTestCaseManager;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import junit.framework.TestCase;

public class GenerateDebugScriptIT extends TestCase {
	@Ignore
	@Test
	public void testCreateDebugScriptMAT510RS() {
//		File file = new File("./src/test/resources/compilelistings/mat510rs_compile_listing.txt");
//		File testScriptFile = new File("./src/test/resources/testcases/mat510rs_testcase.json");
//		try {
//			CompileListing compileListing = Parse.parse(file);
//			BecutTestCase testCase = BecutTestCaseManager.loadTestCase(testScriptFile);
//			
//			DebugScript debugScript = ScriptGenerator.generateDebugScript(compileListing, testCase);
//			String actualScript =debugScript.generate();
//			String expectedScript = new String(Files.readAllBytes(Paths.get("./src/test/resources/debugscripts/mat510rs_debugscript.txt")), StandardCharsets.UTF_8);
//
//			assertEquals(expectedScript, actualScript);
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}	
	}
	
	@Ignore
	@Test
	public void testCreateDebugScriptMAT512RS() {
//		File file = new File("./src/test/resources/compilelistings/mat512rs_compile_listing.txt");
//		File testScriptFile = new File("./src/test/resources/testcases/mat512rs_testcase.json");
//		try {
//			CompileListing compileListing = Parse.parse(file);
//			BecutTestCase testCase = BecutTestCaseManager.loadTestCase(testScriptFile);
//			
//			DebugScript debugScript = ScriptGenerator.generateDebugScript(compileListing, testCase);
//			String actualScript =debugScript.generate();
//			String expectedScript = new String(Files.readAllBytes(Paths.get("./src/test/resources/debugscripts/mat512rs_debugscript.txt")), StandardCharsets.UTF_8);
//
//			assertEquals(expectedScript, actualScript);
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}	
	}

	@Test
	public void testCreateDebugScriptMAT561() throws Exception {
		File file = new File("./src/test/resources/compilelistings/mat561_compile_listing.txt");
		File testScriptFile = new File("./src/test/resources/testcases/mat561_testcase.json");
		CompileListing compileListing = Parse.parse(file);
		BecutTestCase testCase = BecutTestCaseManager.loadTestCase(testScriptFile.toPath());
		testCase.setCompileListing(compileListing);
		
		DebugScript debugScript = ScriptGenerator.generateDebugScript(testCase);
		String actualScript = debugScript.generate();
		System.out.println(actualScript);
//		String expectedScript = new String(Files.readAllBytes(Paths.get("./src/test/resources/debugscripts/mat512rs_debugscript.txt")), StandardCharsets.UTF_8);
//
//		assertEquals(expectedScript, actualScript);
	}
}
