package dk.bec.unittest.becut.debugscript.model;

public class SetSyndebugOff implements DebugEntity {

	@Override
	public String generate() {
		return "           SET DYNDEBUG OFF;";
	}

}
