package dk.bec.unittest.becut.recorder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import dk.bec.unittest.becut.recorder.model.SessionCall;
import dk.bec.unittest.becut.recorder.model.SessionRecord;
import dk.bec.unittest.becut.recorder.model.SessionRecording;
import junit.framework.TestCase;

public class DebugToolLogParserTest extends TestCase {

	public void testParseMAT510Recording() {
		try {
			byte[] fileContentsUnencoded = Files.readAllBytes(Paths.get("./src/test/resources/parameter_recordings/mat510.txt"));
			String fileContents = new String(fileContentsUnencoded, StandardCharsets.UTF_8);
			SessionRecording recording = DebugToolLogParser.Parse(fileContents, "filename");
			List<SessionRecord> changedSessionRecords = new ArrayList<SessionRecord>();
			for (SessionCall sessionCall: recording.getSessionCalls()) {
				changedSessionRecords.addAll(sessionCall.getChangedParameters());
			}
			System.out.println("We're done");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
