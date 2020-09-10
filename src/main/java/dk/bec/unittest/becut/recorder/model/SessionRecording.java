package dk.bec.unittest.becut.recorder.model;

import java.util.ArrayList;
import java.util.List;

public class SessionRecording {
	private String programName;

	private List<SessionCall> sessionCalls = new ArrayList<>();
	private List<SessionPostCondition> sessionPostConditions = new ArrayList<>();
	
	public SessionRecording() {
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}
	
	public String getProgramName() {
		return programName;
	}

	public List<SessionCall> getSessionCalls() {
		return sessionCalls;
	}

	public List<SessionPostCondition> getSessionPostConditions() {
		return sessionPostConditions;
	}

	private SessionCallPart sessionCallPart;
	public SessionCallPart getAfter() {
		return sessionCallPart;
	}

	public void setAfter(SessionCallPart sessionCallPart) {
		this.sessionCallPart = sessionCallPart;
	}
	
	@Override
	public String toString() {
		return "SessionRecording [programName=" + programName + ", sessionCalls=" + sessionCalls
				+ ", sessionPostConditions=" + sessionPostConditions + "]";
	}
}
