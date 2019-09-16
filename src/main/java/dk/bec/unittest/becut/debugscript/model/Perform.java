package dk.bec.unittest.becut.debugscript.model;

import java.util.List;

public class Perform implements DebugEntity {

	private List<Statement> statements;

	public Perform(List<Statement> statements) {
		super();
		this.statements = statements;
	}

	public List<Statement> getStatements() {
		return statements;
	}

	public void setStatements(List<Statement> statements) {
		this.statements = statements;
	}

	@Override
	public String generate() {
		String perform = "           PERFORM\n";
		for (Statement statement : statements) {
			perform = perform + "\n" + statement.generate();
		}
		perform = perform + "\n           END-PERFORM;";
		return perform;
	}

}
