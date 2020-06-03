package dk.bec.unittest.becut.debugscript.model.conditional;

import dk.bec.unittest.becut.debugscript.model.Expression;

public class EqualsConditional extends BinaryConditional {

	public EqualsConditional(Expression leftSide, Expression rightSide) {
		this.leftSide = leftSide;
		this.rightSide = rightSide;
	}

	@Override
	public String generate() {
		return leftSide.generate() +  " = " + rightSide.generate();
	}

}
