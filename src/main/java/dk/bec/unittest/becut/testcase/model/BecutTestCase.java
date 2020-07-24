package dk.bec.unittest.becut.testcase.model;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.bec.unittest.becut.compilelist.model.CompileListing;

public class BecutTestCase {
	private String testCaseName;
	private String testCaseId;
	private String programName;
	private PreCondition preCondition;
	private List<ExternalCall> externalCalls = new ArrayList<>();
	private PostCondition postCondition;
	//SELECT NUM-LIST ASSIGN TO INPUT1.
	//needed for getting record size info from the AST of the source
	private Map<String, String> fileControlAssignment = new HashMap<>();
	private Map<String, File> assignmentLocalFile = new HashMap<>();
	private CompileListing compileListing;
	private Path debugScriptPath;
	private Path becutTestCaseDir = Paths.get(System.getProperty("java.io.tmpdir"));
	
	public Path getBecutTestCaseDir() {
		return becutTestCaseDir;
	}

	public void setBecutTestCaseDir(Path becutTestCaseDir) {
		this.becutTestCaseDir = becutTestCaseDir;
	}

	public Path getDebugScriptPath() {
		return debugScriptPath;
	}

	public void setDebugScriptPath(Path debugScriptPath) {
		this.debugScriptPath = debugScriptPath;
	}

	public CompileListing getCompileListing() {
		return compileListing;
	}

	public void setCompileListing(CompileListing compileListing) {
		this.compileListing = compileListing;
	}

	public Map<String, String> getFileControlAssignments() {
		return fileControlAssignment;
	}

	public void setFileControlAssignment(Map<String, String> fileControlAssignment) {
		this.fileControlAssignment = fileControlAssignment;
	}

	public Map<String, File> getAssignmentLocalFile() {
		return assignmentLocalFile;
	}

	public void setAssignmentLocalFile(Map<String, File> assignmentLocalFile) {
		this.assignmentLocalFile = assignmentLocalFile;
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
}
