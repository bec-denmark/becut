package dk.bec.unittest.becut.debugscript.model.variable;

public class Literal extends Variable {

	String value;

	public Literal(String value) {
		super();
		this.value = value;
	}

	@Override
	public String generate() {
		return value;
	}

	@Override
	public String declaration() {
		return "";
	}

}
