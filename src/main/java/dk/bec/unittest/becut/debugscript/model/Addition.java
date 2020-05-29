package dk.bec.unittest.becut.debugscript.model;

import dk.bec.unittest.becut.debugscript.model.variable.VariableOrLiteral;

public class Addition implements Expression {

	private VariableOrLiteral leftSide;
	private VariableOrLiteral rightSide;

	public Addition(VariableOrLiteral leftSide, VariableOrLiteral rightSide) {
		this.leftSide = leftSide;
		this.rightSide = rightSide;
	}

	@Override
	public String generate() {
		return leftSide + " + " + rightSide.generate();
	}

}
