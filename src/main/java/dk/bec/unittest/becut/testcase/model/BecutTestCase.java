package dk.bec.unittest.becut.testcase.model;

import java.util.ArrayList;
import java.util.List;

public class BecutTestCase {

	private String testCaseName;
	private String testCaseId;
	private String programName;
	private PreConditon preConditon;
	private PostCondition postCondition;
	private List<ExternalCall> externalCalls = new ArrayList<>();

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

	public PreConditon getPreConditon() {
		return preConditon;
	}

	public void setPreConditon(PreConditon preConditon) {
		this.preConditon = preConditon;
	}

	public PostCondition getPostCondition() {
		return postCondition;
	}

	public void setPostCondition(PostCondition postCondition) {
		this.postCondition = postCondition;
	}

}
