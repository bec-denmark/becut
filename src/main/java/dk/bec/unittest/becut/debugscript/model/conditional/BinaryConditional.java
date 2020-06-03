package dk.bec.unittest.becut.debugscript.model.conditional;

import dk.bec.unittest.becut.debugscript.model.Expression;

public abstract class BinaryConditional implements Conditional {
	protected Expression leftSide;
	protected Expression rightSide;
}
