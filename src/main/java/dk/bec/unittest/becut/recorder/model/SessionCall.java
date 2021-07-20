package dk.bec.unittest.becut.recorder.model;

import java.util.List;

public class SessionCall {

	private String calleeProgramName;
	private Integer lineNumber;
	
	//A statement_id identifies an executable statement. 
	//The statement id is an integer or integer.integer (where the first integer is the line number and the second integer is the relative statement number). 
	private String statementId;
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

	public String getStatementId() {
		return statementId;
	}
	
	public void setStatementId(String statementId) {
		this.statementId = statementId;
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

	public List<SessionRecord> getAfterParameters() {
		return after.getRecords();
	}

	@Override
	public String toString() {
		return "{SessionCall: " + lineNumber + ":" + calleeProgramName + ":" + iteration + "}";
	}
}
