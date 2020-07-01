package dk.bec.unittest.becut.debugscript.model.statement;

import dk.bec.unittest.becut.testcase.model.Parameter;
import dk.bec.unittest.becut.recorder.DebugToolLogParser;

public class Assertion extends StatementBase {

	private String name;
	
	public Assertion(Parameter parameter) {
		this.name = parameter.getName();
	}
	@Override
	public String generate() {
		String result = "";
		result += "       LIST (\"" + DebugToolLogParser.BEGIN_POST_CONDITION + "\");\n";
		result += "       LIST (" + name + ");\n";
		result += "       LIST (\"" + DebugToolLogParser.END_POST_CONDITION + "\");\n";
		return result;
	}

}
