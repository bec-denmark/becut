package dk.bec.unittest.becut.testcase.model;

import java.util.List;
import java.util.stream.Collectors;

import dk.bec.unittest.becut.debugscript.model.CallType;

public class ExternalCall {

	private String name;
	private String displayableName;
	private Integer lineNumber;
	private CallType callType;
	private List<Parameter> parameters;

	public ExternalCall(String name, Integer lineNumber, CallType callType, List<Parameter> parameters) {
		this.name = name;
		this.displayableName = name;
		this.lineNumber = lineNumber;
		this.callType = callType;
		this.parameters = parameters;
	}

	public ExternalCall(String name, String displayableName, Integer lineNumber, CallType callType, List<Parameter> parameters) {
		this.name = name;
		this.displayableName = displayableName;
		this.lineNumber = lineNumber;
		this.callType = callType;
		this.parameters = parameters;
	}

	public ExternalCall() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayableName() {
		return displayableName;
	}

	public void setDisplayableName(String displayableName) {
		this.displayableName = displayableName;
	}

	public Integer getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	public CallType getCallType() {
		return callType;
	}

	public void setCallType(CallType callType) {
		this.callType = callType;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
	
	@Override
	public String toString() {
		return "CALL " + name + " USING " + String.join(" ", parameters.stream().map(Parameter::getName).collect(Collectors.toList()));
	}

}
