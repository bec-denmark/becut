package dk.bec.unittest.becut.debugscript.model.statement;

import dk.bec.unittest.becut.debugscript.model.DebugEntity;

public class SetLogOnFile extends StatementBase implements DebugEntity {
	private String dd;

	public SetLogOnFile(String dd) {
		this.dd = dd;
	}

	@Override
	public String generate() {
		return "        SET LOG ON FILE " + dd + ";";
	}

}
