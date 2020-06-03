package dk.bec.unittest.becut.debugscript.model;

import dk.bec.unittest.becut.debugscript.model.statement.Statement;

public class DebugEntityStatementContainer implements DebugEntity {
	
	private Statement statement;
	
	

	public DebugEntityStatementContainer(Statement statement) {
		this.statement = statement;
	}



	@Override
	public String generate() {
		return statement.generate();
	}

}
