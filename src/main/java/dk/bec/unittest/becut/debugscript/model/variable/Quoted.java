package dk.bec.unittest.becut.debugscript.model.variable;

public class Quoted extends Variable {

	String value;

	public Quoted(String value) {
		this.value = "'" + value + "'";
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
