package dk.bec.unittest.becut.recorder.model;

import java.util.ArrayList;
import java.util.List;

public class SessionRecording {
	
	private String programName;
	List<SessionCall> sessionCalls = new ArrayList<SessionCall>();

	public SessionRecording(String programName) {
		this.programName = programName;
	}

	public String getProgramName() {
		return programName;
	}

	public List<SessionCall> getSessionCalls() {
		return sessionCalls;
	}
}
