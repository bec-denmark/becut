package dk.bec.unittest.becut.debugscript.model.variable;


public abstract class Variable extends VariableOrLiteral {

	protected String name;
	protected String type;

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

}
