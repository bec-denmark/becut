package dk.bec.unittest.becut.recorder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dk.bec.unittest.becut.recorder.model.SessionCall;
import dk.bec.unittest.becut.recorder.model.SessionCallPart;
import dk.bec.unittest.becut.recorder.model.SessionPostCondition;
import dk.bec.unittest.becut.recorder.model.SessionRecord;
import dk.bec.unittest.becut.recorder.model.SessionRecording;

public class DebugToolLogParser {
	public static final String BEGIN_POST_CONDITION = "Start BECUT PostCondition";
	public static final String END_POST_CONDITION = "End BECUT PostCondition";
	
	private static final String BEGIN_BEFORE_CALL = "BEGIN BEFORE CALL";
	private static final String END_BEFORE_CALL = "END BEFORE CALL";
	private static final String BEGIN_AFTER_CALL = "BEGIN AFTER CALL";
	private static final String END_AFTER_CALL = "END AFTER CALL";

	private static final String BEGIN_EXIT = "BEGIN EXIT";
	private static final String END_EXIT = "END EXIT";
	
	private static final Pattern AT_LINE = Pattern.compile("At LINE ((\\d+)\\.\\d+) in COBOL program (.*) ::> (.*)\\."); 
	private static final Pattern AT_EXIT = Pattern.compile("At EXIT in COBOL program (.*) ::> (.*)\\.");
	
	public static SessionRecording parseRecording(String debugToolLog) throws ParsingException {
		SessionRecording sessionRecording = new SessionRecording();
		List<String> logLines = splitLines(debugToolLog);

		int iteration = 0;
		Integer lineNumber = null;
		Integer previousLine = null;
		
		final int LA_BEGIN_BEFORE_CALL = 0;
		final int LA_END_BEFORE_CALL = 2;
		final int LA_BEGIN_AFTER_CALL = 3;
		final int LA_END_AFTER_CALL = 5;		
		final int LA_END_EXIT = 7;
		
		int state = LA_BEGIN_BEFORE_CALL;
		
		List<String> titled = null;
		SessionCall sc = null;
		
		for(LineCountingIterator it = new LineCountingIterator(logLines.iterator()); it.hasNext();) {
			String line = removeProlog(it.next());
			switch(state) {
				case LA_BEGIN_BEFORE_CALL :
					if(line.startsWith(BEGIN_BEFORE_CALL) || line.startsWith(BEGIN_EXIT)) {
						line = removeProlog(it.next());
						Matcher at_line_matcher = AT_LINE.matcher(line);
						Matcher at_exit_matcher = AT_EXIT.matcher(line);
						if(at_line_matcher.find()) {
							lineNumber = Integer.parseInt(at_line_matcher.group(2));
							sessionRecording.setProgramName(at_line_matcher.group(4));
							titled = new ArrayList<>();
							sc = new SessionCall();
							sc.setLineNumber(lineNumber);
							sc.setStatementId(at_line_matcher.group(1));
							if(!Objects.equals(previousLine, lineNumber)) {
								iteration = 0;
							} else {
								iteration++;
							}
							sc.setIteration(iteration);
							previousLine = lineNumber;
							state = LA_END_BEFORE_CALL;
						} else if(at_exit_matcher.find()) {
							sessionRecording.setProgramName(at_exit_matcher.group(1));
							titled = new ArrayList<>();
							state = LA_END_EXIT;
						} else {
							throw new ParsingException("missing LINE info at line " + it.getLineNumber());
						}
					}
					break;
				case LA_END_BEFORE_CALL :
					if(line.startsWith(END_BEFORE_CALL)) {
						state = LA_BEGIN_AFTER_CALL;
						sc.setBefore(new SessionCallPart(titled)); 
					} else {
						titled.add(line);
					}
					break;
				case LA_END_EXIT :
					if(line.startsWith(END_EXIT)) {
						sessionRecording.setAfter(new SessionCallPart(titled));
						break;
					} else {
						titled.add(line);
					}
					break;
				case LA_BEGIN_AFTER_CALL :
					if(line.startsWith(BEGIN_AFTER_CALL)) {
						line = removeProlog(it.next());
						Matcher at_line_matcher = AT_LINE.matcher(line);
						if(at_line_matcher.find()) {
							state = LA_END_AFTER_CALL;
							titled = new ArrayList<>();
						} else {
							throw new ParsingException("missing LINE info at line " + it.getLineNumber());
						}
					}
					break;
				case LA_END_AFTER_CALL :
					if(line.startsWith(END_AFTER_CALL)) {
						state = LA_BEGIN_BEFORE_CALL;
						sc.setAfter(new SessionCallPart(titled));
						sessionRecording.getSessionCalls().add(sc);
					} else {
						titled.add(line);
					}
					break;
			}
		}
		return sessionRecording;
	}

	public static SessionRecording parseRunning(List<String> logLines) throws ParsingException {
		SessionRecording sessionRecording = new SessionRecording();
		
		final int LA_BEGIN_POST_CONDITION = 1;		
		final int LA_END_POST_CONDITION = 2;
		
		int state = LA_BEGIN_POST_CONDITION;

		for(LineCountingIterator it = new LineCountingIterator(logLines.iterator()); it.hasNext();) {
			String logLine = removeProlog(it.next()).trim();
			switch(state) {
				case LA_BEGIN_POST_CONDITION :
					if(BEGIN_POST_CONDITION.equals(logLine)) {
						state = LA_END_POST_CONDITION;
					}
					break;
				case LA_END_POST_CONDITION:
					if(END_POST_CONDITION.equals(logLine)) {
						state = LA_BEGIN_POST_CONDITION;
					} else {
						SessionPostCondition sessionPostCondition = new SessionPostCondition();
						String[] parts = logLine.split("=");
						String variableName = parts[0].trim();
						if(parts.length != 2) {
							//variable was not initialized:
							//* possible mismatch between the listing and the actual load module
							//* something wrong with the debug script
							String value = null;
							SessionRecord record = new SessionRecord(-1, "", variableName, value, null, null);
							sessionPostCondition.getSessionRecords().add(record);
							sessionRecording.getSessionPostConditions().add(sessionPostCondition);
						} else {
							String value = parts[1].trim();
							SessionRecord record = new SessionRecord(-1, "", variableName, value, null, null);
							sessionPostCondition.getSessionRecords().add(record);
							sessionRecording.getSessionPostConditions().add(sessionPostCondition);
						}
					}
					break;
			}
		}
		return sessionRecording;
	}	
	
	private static Pattern prolog = Pattern.compile("\\s+\\* ");
	public static String removeProlog(String line) {
		Matcher m = prolog.matcher(line);
		if(line != null && m.find()) {
			return line.substring(m.end());
		}
		return line;
	}

	public static List<String> splitLines(String s) {
		if(s == null || s.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<String> lines = new ArrayList<>();
		
		int start = 0;
		for(int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if(c == '\n') {
				if(i > 0 && s.charAt(i - 1) == '\r') {
					lines.add(s.substring(start, i - 1));
				} else {
					lines.add(s.substring(start, i));
				}
				start = i + 1;
			}
		}
		if(start < s.length()) {
			lines.add(s.substring(start, s.length()));
		}
		return lines;
	}
	
	//counting iterator for a better diagnostics of parsing debug tool log problems
	private static class LineCountingIterator implements Iterator<String> {
		private Iterator<String> it;
		private int lineNumber = 0;
		LineCountingIterator(Iterator<String> it) {
			this.it = it;
		}
		
		@Override
		public boolean hasNext() { return it.hasNext(); }

		@Override
		public String next() {
			if(it.hasNext()) {
				lineNumber++;
			}
			return it.next();
		}
		
		public int getLineNumber() { return lineNumber; }
	}
	
	public static class ParsingException extends RuntimeException {
		public ParsingException(String message) {
			super(message);
		}
	}
}
