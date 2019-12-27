package dk.bec.unittest.becut.debugscript.model;

import dk.bec.unittest.becut.testcase.model.Parameter;

public class Assertion implements Statement {

	private String name;
	private String comparisonOperator;
	private String expectedValue;
	
	public Assertion(Parameter parameter) {
		this.name = parameter.getName();
		this.comparisonOperator = " = ";
		this.expectedValue = parameter.getValue();
	}
	@Override
	public String generate() {
		return "       LIST " + name + comparisonOperator + expectedValue + ";";
	}

}
