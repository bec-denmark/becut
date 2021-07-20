package dk.bec.unittest.becut.debugscript.model.statement;

import dk.bec.unittest.becut.debugscript.model.Expression;
import dk.bec.unittest.becut.debugscript.model.variable.Variable;

public class Compute extends StatementBase {

	private Variable leftSide;
	private Expression rightSide;

	public Compute(Variable leftSide, Expression rightSide) {
		this.leftSide = leftSide;
		this.rightSide = rightSide;
	}

	@Override
	public String generate() {
		String result = "";
		result += "        COMPUTE " + leftSide.getName() + " = \n";
		result += "        " + rightSide.generate() + ";";
		return result;
	}

}
