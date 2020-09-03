package dk.bec.unittest.becut.debugscript.model.statement;

import dk.bec.unittest.becut.debugscript.model.DebugEntity;

public class Quit implements DebugEntity {

	@Override
	public String generate() {
		return "          QUIT;";
	}

}
