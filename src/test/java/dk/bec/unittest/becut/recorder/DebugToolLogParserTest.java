package dk.bec.unittest.becut.recorder;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;

import dk.bec.unittest.becut.recorder.model.SessionRecording;

public class DebugToolLogParserTest {
	@Ignore
	@Test
	public void testParseMAT561Recording() throws Exception {
		byte[] fileContentsUnencoded = Files.readAllBytes(Paths.get("./src/test/resources/parameter_recordings/mat561.txt"));
		String fileContents = new String(fileContentsUnencoded, StandardCharsets.UTF_8);
		SessionRecording recording = DebugToolLogParser.parseRecording(fileContents);
		assertEquals("Program name", "MAT561", recording.getProgramName());
		assertEquals("Session calls count", 3, recording.getSessionCalls().size());
	}

	@Ignore
	@Test
	public void testParseSameCallDifferentLines() throws Exception {
		byte[] fileContentsUnencoded = Files.readAllBytes(Paths.get("./src/test/resources/parameter_recordings/same_call_different_lines.txt"));
		String fileContents = new String(fileContentsUnencoded, StandardCharsets.UTF_8);
		SessionRecording recording = DebugToolLogParser.parseRecording(fileContents);
		assertEquals("Program name", "MAT561", recording.getProgramName());
		assertEquals("Session calls count", 4, recording.getSessionCalls().size());
	}
	
	@Ignore
	@Test
	public void testRecurentCalls() throws Exception {
		byte[] fileContentsUnencoded = Files.readAllBytes(Paths.get(
				"./src/test/resources/parameter_recordings/MAT563.INSPLOG.BECUT.T366993"));
		String fileContents = new String(fileContentsUnencoded, StandardCharsets.UTF_8);
		SessionRecording recording = DebugToolLogParser.parseRecording(fileContents);
		assertEquals("Program name", "MAT563", recording.getProgramName());
		assertEquals("Session calls count", 4, recording.getSessionCalls().size());
	}

	@Ignore
	@Test
	public void testRDZDB2() throws Exception {
		byte[] fileContentsUnencoded = Files.readAllBytes(Paths.get(
				"./src/test/resources/parameter_recordings/RDZDB2.txt"));
		String fileContents = new String(fileContentsUnencoded, StandardCharsets.UTF_8);
		SessionRecording recording = DebugToolLogParser.parseRecording(fileContents);
		assertEquals("Program name", "RDZDB2", recording.getProgramName());
		assertEquals("Session calls count", 13, recording.getSessionCalls().size());
	}

	@Test
	public void testSUMER() throws Exception {
		byte[] fileContentsUnencoded = Files.readAllBytes(Paths.get(
				"./src/test/resources/parameter_recordings/SUMER.txt"));
		String fileContents = new String(fileContentsUnencoded, StandardCharsets.UTF_8);
		SessionRecording recording = DebugToolLogParser.parseRecording(fileContents);
		assertEquals("Program name", "SUMER", recording.getProgramName());
		assertEquals("Session calls count", 0, recording.getSessionCalls().size());
		//assertEquals("", "006", recording.getSessionPostConditions().
	}
}
