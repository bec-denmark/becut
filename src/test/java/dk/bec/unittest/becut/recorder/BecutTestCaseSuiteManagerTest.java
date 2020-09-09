package dk.bec.unittest.becut.recorder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import dk.bec.unittest.becut.compilelist.Parse;
import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.recorder.model.SessionRecording;
import dk.bec.unittest.becut.testcase.BecutTestCaseSuiteManager;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;

public class BecutTestCaseSuiteManagerTest {
	@Test
	public void testRDZDB2() throws Exception {
		byte[] fileContentsUnencoded = Files.readAllBytes(
				Paths.get("./src/test/resources/parameter_recordings/RDZDB2.txt"));
		String fileContents = new String(fileContentsUnencoded, StandardCharsets.UTF_8);
		SessionRecording sessionRecording = DebugToolLogParser.parseRecording(fileContents);
		
		File file = new File("./src/test/resources/compilelistings/RDZDB2.txt");
		CompileListing compileListing = Parse.parse(file);
		
		BecutTestCase testCase = BecutTestCaseSuiteManager.createTestCaseFromSessionRecording(compileListing, sessionRecording);
		System.out.println(testCase);
	}
}
