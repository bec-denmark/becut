package dk.bec.unittest.becut.debugscript.model;

public class Quit implements DebugEntity {

	@Override
	public String generate() {
		return "           QUIT;";
	}

}
