package dk.bec.unittest.becut.recorder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dk.bec.unittest.becut.recorder.model.SessionCall;
import dk.bec.unittest.becut.recorder.model.SessionCallPart;
import dk.bec.unittest.becut.recorder.model.SessionRecording;

public class DebugToolLogParser {
	
	public static final String LOG_OUTPUT_START_MARKER = "*** Commands file commands end ***";
	public static final String START_CALL_MARKER = "START CALL";
	public static final String END_CALL_MARKER = "END CALL";
	public static final String START_AFTER_CALL_MARKER = "START AFTER CALL";
	public static final String END_AFTER_CALL_MARKER = "END AFTER CALL";
	
	private static Pattern HEADER_PATTERN = Pattern.compile("^.*CALL (\\d+):(.*)$");
	
	public static SessionRecording Parse(String debugToolLog, String programName) {
		SessionRecording sessionRecording = new SessionRecording(programName);
		List<String> logLines = new ArrayList<String>(Arrays.asList(debugToolLog.split("\\n")));
		List<String> cleanLog = cleanLog(logLines);
		List<List<String>> callBlocks = splitOnCallBlocks(cleanLog);
		SessionCall currentSessionCall = new SessionCall();
		for (List<String> callBlock: callBlocks) {
			
			Matcher matcher = HEADER_PATTERN.matcher(callBlock.get(0));
			if (matcher.matches()) {
				if (callBlock.get(0).startsWith(START_CALL_MARKER)) {
					// we are in the before pattern
					currentSessionCall = new SessionCall();
					sessionRecording.getSessionCalls().add(currentSessionCall);
					SessionCallPart before = new SessionCallPart(callBlock);
					currentSessionCall.setBefore(before);

					currentSessionCall.setIteration(Integer.parseInt(callBlock.get(1).split("=")[1].trim()));

					int lineNumber = Integer.parseInt(matcher.group(1));
					currentSessionCall.setLineNumber(lineNumber);

					String calleeName = matcher.group(2);
					currentSessionCall.setCalleeProgramName(calleeName);
					
				} else if (callBlock.get(0).startsWith(START_AFTER_CALL_MARKER)) {
				// we are in the after pattern
					SessionCallPart after = new SessionCallPart(callBlock);
					currentSessionCall.setAfter(after);
				}
			}
		}
		
		return sessionRecording;
	}

	private static List<String> cleanLog(List<String> logLines) {
		boolean logStart = false;
		List<String> cleanLines = new ArrayList<String>();
		int currentActualLine = -1;
		for (int i = 0; i < logLines.size(); i++) {
			if (logStart) {
				StringBuilder l = new StringBuilder(logLines.get(i));
				if (l.length() > 7 && l.charAt(6) == '*') {
					l.setCharAt(6, ' ');
				}
				String line = l.toString().trim();
				if (nonDataLine(line) || line.matches("^\\d{2}.*$")) {
					cleanLines.add(line);
					currentActualLine++;
				} else {
					cleanLines.set(currentActualLine, cleanLines.get(currentActualLine) + " " + line);
				}
			}
			
			//We found the point in the log we are interested in and can start using the above code
			if (logLines.get(i).contains(LOG_OUTPUT_START_MARKER)) {
				logStart = true;
			}
		}
		
		return cleanLines;
	}
	
	private static List<List<String>> splitOnCallBlocks(List<String> logLines) {
		List<List<String>> callBlocks = new ArrayList<List<String>>();
		List<String> block = new ArrayList<String>();
		for (String line: logLines) {
			if (line.startsWith(START_CALL_MARKER) || line.startsWith(START_AFTER_CALL_MARKER)) {
				block = new ArrayList<String>();
				callBlocks.add(block);
			}
			block.add(line);
		}
		return callBlocks;
	}

	private static boolean nonDataLine(String line) {
		return line.startsWith("GO") || line.startsWith(START_CALL_MARKER) || line.startsWith(START_AFTER_CALL_MARKER) || line.startsWith(END_CALL_MARKER) || line.startsWith(END_AFTER_CALL_MARKER) || line.startsWith(RecorderManager.ITERATION_COUNTER_PREFIX);
	}
}
