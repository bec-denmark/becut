package dk.bec.unittest.becut.recorder;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dk.bec.unittest.becut.recorder.model.SessionCall;
import dk.bec.unittest.becut.recorder.model.SessionCallPart;
import dk.bec.unittest.becut.recorder.model.SessionPostCondition;
import dk.bec.unittest.becut.recorder.model.SessionRecord;
import dk.bec.unittest.becut.recorder.model.SessionRecording;
import dk.bec.unittest.becut.testcase.SessionRecordingException;

public class DebugToolLogParser {
	public static final String BEGIN_POST_CONDITION = "Start BECUT PostCondition";
	public static final String END_POST_CONDITION = "End BECUT PostCondition";
	
	private static final String AT_CALL_BEGIN = "AT CALL BEGIN";
	private static final String AT_CALL_END = "AT CALL END";
	private static final String AT_EXIT_BEGIN = "AT EXIT BEGIN";
	private static final String AT_EXIT_END = "AT EXIT END";
	private static final Pattern AT_EXIT_PROGRAM = Pattern.compile("At EXIT in COBOL program (.*) ::> (.*)\\.");
	//line may be 45.2, it means second statement on line 45 like here:
	//DISPLAY 'HI' CALL TMAT5110 USING MAT511-AREA
	//let's assume that everybody is reasonable and there is only one call per line
	private static final Pattern AT_LINE = Pattern.compile("At LINE ((\\d+)\\.\\d+) in COBOL program (.*) ::> (.*)\\."); 
	private static final Pattern FROM_LINE = Pattern.compile("From LINE (\\d+)\\.\\d+ in COBOL program (.*) ::> (.*)\\.");
	
	public static SessionRecording parseRecording(String debugToolLog, String programName) throws LogParsingException {
		SessionRecording sessionRecording = new SessionRecording();
		List<String> logLines = Arrays.asList(debugToolLog.split("\\r?\\n"));

		int iteration = 0;
		Integer lineNumber = null;
		Integer previousLine = null;
		
		final int LA_AT_CALL_BEGIN = 0;
		final int LA_LINE_WITHIN_AT_CALL = 1;
		final int LA_AT_CALL_END = 2;
		final int LA_AT_EXIT_BEGIN = 3;
		final int LA_LINE_WITHIN_AT_EXIT = 4;
		final int LA_AT_EXIT_END = 5;		
		final int LA_AT_PROGRAM_EXIT_END = 6;
		
		int state = LA_AT_CALL_BEGIN;
		
		List_Titled titled = null;
		SessionCall sc = null;
		
		for(LineCountingIterator it = new LineCountingIterator(logLines.iterator()); it.hasNext();) {
			String logLine = removeProlog(it.next());
			switch(state) {
				case LA_AT_CALL_BEGIN :
					if(AT_CALL_BEGIN.equals(logLine)) {
						state = LA_LINE_WITHIN_AT_CALL;
					}
					break;
				case LA_LINE_WITHIN_AT_CALL :
					Matcher at_line_matcher = AT_LINE.matcher(logLine);
					if(at_line_matcher.find()) {
						lineNumber = Integer.parseInt(at_line_matcher.group(2));
						sessionRecording.setProgramName(at_line_matcher.group(4));
						titled = new List_Titled();
						sc = new SessionCall();
						sc.setLineNumber(lineNumber);
						sc.setStatementId(at_line_matcher.group(1));
						state = LA_AT_CALL_END;
						if(!Objects.equals(previousLine, lineNumber)) {
							iteration = 0;
						} else {
							iteration++;
						}
						sc.setIteration(iteration);
						previousLine = lineNumber;
						state = LA_AT_CALL_END;
					} else {
						throw new LogParsingException("missing LINE info at line " + it.getLineNumber());
					}
					break;
				case LA_AT_CALL_END :
					if(AT_CALL_END.equals(logLine)) {
						state = LA_AT_EXIT_BEGIN;
						sc.setBefore(new SessionCallPart(titled)); 
					} else {
						titled.add(logLine);
					}
					break;
				case LA_AT_EXIT_BEGIN :
					if(AT_EXIT_BEGIN.equals(logLine)) {
						state = LA_LINE_WITHIN_AT_EXIT;
					}
					break;
				case LA_LINE_WITHIN_AT_EXIT :
					Matcher from_line_matcher = FROM_LINE.matcher(logLine);
					Matcher at_exit_program_matcher = AT_EXIT_PROGRAM.matcher(logLine);
					if(from_line_matcher.find()) {
						state = LA_AT_EXIT_END;
						sc.setCalleeProgramName(from_line_matcher.group(2));
						titled = new List_Titled();
					} else if(at_exit_program_matcher.find()){
						if(at_exit_program_matcher.group(2).equals(sessionRecording.getProgramName())) {
							state = LA_AT_PROGRAM_EXIT_END;
						}
					}
					break;
				case LA_AT_EXIT_END :
					if(AT_EXIT_END.equals(logLine)) {
						state = LA_AT_CALL_BEGIN;
						sc.setAfter(new SessionCallPart(titled));
						sessionRecording.getSessionCalls().add(sc);
					} else {
						titled.add(logLine);
					}
					break;
				case LA_AT_PROGRAM_EXIT_END :
					break;
			}
		}
		return sessionRecording;
	}

	public static SessionRecording parseRunning(String debugToolLog, String programName) throws LogParsingException {
		SessionRecording sessionRecording = new SessionRecording();
		List<String> logLines = Arrays.asList(debugToolLog.split("\\r?\\n"));
		
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
						try {
							SessionPostCondition sessionPostCondition = new SessionPostCondition();
							String[] parts = logLine.split("=");
							assert parts != null && parts.length == 2;
							String variableName = parts[0].trim();
							String value = parts[1].trim();
							SessionRecord record = new SessionRecord(-1, "", variableName, value, null, null);
							sessionPostCondition.getSessionRecords().add(record);
							sessionRecording.getSessionPostConditions().add(sessionPostCondition);
						} catch (Exception e) {
							throw new SessionRecordingException("expected: assignement at line " + it.getLineNumber()
									+ ", actual: " + logLine);
						}
					}
					break;
			}
		}
		return sessionRecording;
	}	
	
	//a class for storing what's produced by LIST TITLED * debug tool command
	private static class List_Titled extends LinkedList<String> {
		@Override
		public boolean add(String e) {
			if(size() > 0) {
				String previous = peekLast();
				//list for nested records can take more than one line:
			    //03 MAT561:>MAT511-SUM  of 02 MAT561:>MAT511-DATA  of 01
			    //MAT561:>MAT511-AREA  = 0000000001
				//.. let's join them
				if(previous.matches(".* of \\d+")) {
					super.add(pollLast() + " " + e);
				} else {
					super.add(e);
				}
			} else if(e.matches("\\d+ .*")) {
				return super.add(e);
			}
			return false;
		}
	}
	
	//debug log starts with '      * '
	private static String removeProlog(String line) {
		String prolog = "      * ";
		if(line != null && line.startsWith(prolog)) {
			return line.substring(prolog.length());
		}
		return line;
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
}
