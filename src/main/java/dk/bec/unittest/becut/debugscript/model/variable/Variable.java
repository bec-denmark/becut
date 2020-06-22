package dk.bec.unittest.becut.debugscript.model.variable;


public abstract class Variable extends VariableOrLiteral {

	protected String name;
	protected String type;
	protected String defaultValue;

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}
	
	public abstract String declaration();

}
