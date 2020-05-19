package dk.bec.unittest.becut.debugscript.model;

import dk.bec.unittest.becut.testcase.model.Parameter;
import dk.bec.unittest.becut.recorder.DebugToolLogParser;

public class Assertion implements Statement {

	private String name;
	
	public Assertion(Parameter parameter) {
		this.name = parameter.getName();
	}
	@Override
	public String generate() {
		String result = "";
		result += "       LIST (\"" + DebugToolLogParser.START_POST_CONDITION + "\");\n";
		result += "       LIST (" + name + ");";
		return result;
	}

}
