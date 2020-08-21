package dk.bec.unittest.becut.testcase.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.bec.unittest.becut.debugscript.model.DebugScript;

public class BecutTestCase {
	private String testCaseName;
	private String testCaseId;
	private String programName;
	private PreCondition preCondition;
	private List<ExternalCall> externalCalls = new ArrayList<>();
	private PostCondition postCondition;
	private DebugScript debugScript;

	//SELECT NUM-LIST ASSIGN TO INPUT1.
	//NUM-LIST and INPUT1 assosiation needed for getting record size info from the AST of the source
	private Map<String, String> fileControlAssignments = new HashMap<>();
	
	public Map<String, String> getFileControlAssignments() {
		return fileControlAssignments;
	}

	public void setFileControlAssignments(Map<String, String> fileControlAssignments) {
		this.fileControlAssignments = fileControlAssignments;
	}

	public String getTestCaseName() {
		return testCaseName;
	}

	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}

	public String getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(String testCaseId) {
		this.testCaseId = testCaseId;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public List<ExternalCall> getExternalCalls() {
		return externalCalls;
	}

	public void setExternalCalls(List<ExternalCall> externalCalls) {
		this.externalCalls = externalCalls;
	}
	
	public void addExternalCall(ExternalCall externalCall) {
		this.externalCalls.add(externalCall);
	}
	
	public void removeExternalCall(int lineNumber) {
		boolean found = false;
		int i = 0;
		for (ExternalCall e: externalCalls) {
			if (lineNumber == e.getLineNumber()) {
				found = true;
				break;
			}
			i++;
		}
		if (found) {
			this.externalCalls.remove(i);
		}
	}

	public void removeExternalCall(ExternalCall externalCall) {
		removeExternalCall(externalCall.getLineNumber());
	}

	public PreCondition getPreCondition() {
		return preCondition;
	}

	public void setPreCondition(PreCondition preCondition) {
		this.preCondition = preCondition;
	}

	public PostCondition getPostCondition() {
		return postCondition;
	}

	public void setPostCondition(PostCondition postCondition) {
		this.postCondition = postCondition;
	}
	
	public DebugScript getDebugScript() {
		return debugScript;
	}

	public void setDebugScript(DebugScript debugScript) {
		this.debugScript = debugScript;
	}
}
