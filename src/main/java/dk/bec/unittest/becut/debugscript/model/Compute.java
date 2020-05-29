package dk.bec.unittest.becut.debugscript.model;

import dk.bec.unittest.becut.debugscript.model.variable.Variable;

public class Compute implements Statement {

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
		result += "        " + rightSide.generate() + ";\n";
		return result;
	}

}
