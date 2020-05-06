package dk.bec.unittest.becut.recorder.model;

import java.util.ArrayList;
import java.util.List;

public class SessionCall {
	
	private String calleeProgramName;
	private Integer lineNumber;
	private Integer iteration;
	private SessionCallPart before;
	private SessionCallPart after;
	
	public SessionCall() {

	}

	public String getCalleProgramName() {
		return calleeProgramName;
	}
	public void setCalleeProgramName(String callingProgramName) {
		this.calleeProgramName = callingProgramName;
	}
	public Integer getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}
	public Integer getIteration() {
		return iteration;
	}
	public void setIteration(Integer iteration) {
		this.iteration = iteration;
	}
	public SessionCallPart getBefore() {
		return before;
	}
	public void setBefore(SessionCallPart before) {
		this.before = before;
	}
	public SessionCallPart getAfter() {
		return after;
	}
	public void setAfter(SessionCallPart after) {
		this.after = after;
	}
	
	public List<SessionRecord> getChangedParameters() {
		List<SessionRecord> changedParameters = new ArrayList<SessionRecord>();
		
		for (int i = 0; i < before.getRecords().size(); i++) {
			changedParameters.addAll(compareSessionRecords(before.getRecords().get(i), after.getRecords().get(i)));
		}
		
		return changedParameters;
	}
	
	// Returns a list of session record from sessionRecord2 which are different than sessionRecord1
	private List<SessionRecord> compareSessionRecords(SessionRecord sessionRecord1, SessionRecord sessionRecord2) {
		List<SessionRecord> sessionRecords = new ArrayList<SessionRecord>();
		String v1 = sessionRecord1.getValue();
		String v2 = sessionRecord2.getValue();
		if (v1 != null && !v1.equals(v2)) {
			sessionRecords.add(sessionRecord2);
		} else if (v2 != null && !v2.equals(v1)) {
			sessionRecords.add(sessionRecord2);
		}
		for (int i = 0; i < sessionRecord1.getChildren().size(); i++) {
			sessionRecords.addAll(compareSessionRecords(sessionRecord1.getChildren().get(i), sessionRecord2.getChildren().get(i)));
		}
		
		return sessionRecords;
	}
	
	@Override
	public String toString() {
		return "{SessionCall: " + lineNumber + ":" + calleeProgramName + ":" + iteration + "}"; 
	}
}
