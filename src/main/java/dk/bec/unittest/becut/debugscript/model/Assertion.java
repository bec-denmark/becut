package dk.bec.unittest.becut.debugscript.model;

import dk.bec.unittest.becut.testcase.model.Parameter;

public class Assertion implements Statement {

	private String name;
	
	public Assertion(Parameter parameter) {
		this.name = parameter.getName();
	}
	@Override
	public String generate() {
		return "       LIST (\"PostCondition\", " + name + ");";
	}

}
