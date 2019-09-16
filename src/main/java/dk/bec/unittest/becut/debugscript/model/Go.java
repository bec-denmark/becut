package dk.bec.unittest.becut.debugscript.model;

public class Go implements Statement {

	@Override
	public String generate() {
		return "           GO;";
	}

}
