package dk.bec.unittest.becut.debugscript.model;

import java.util.ArrayList;
import java.util.List;

import dk.bec.unittest.becut.debugscript.model.statement.Assertion;
import dk.bec.unittest.becut.debugscript.model.statement.If;
import dk.bec.unittest.becut.debugscript.model.statement.Statement;

public class Perform implements DebugEntity {

	private List<Statement> statements;

	public Perform(List<Statement> statements) {
		this.statements = statements;
	}

	public Perform(Statement statement) {
		statements = new ArrayList<Statement>();
		statements.add(statement);
	}
	
	public Perform() {
		statements = new ArrayList<Statement>();
	}
	public List<Statement> getStatements() {
		return statements;
	}

	public void setStatements(List<Statement> statements) {
		this.statements = statements;
	}

	@Override
	public String generate() {
		String perform = "";
		if (statements.size() < 2) {
			//FIXME clean up this logic
			if (statements.size() == 1 && (statements.get(0) instanceof Assertion || statements.get(0) instanceof If)) {
				perform = "           PERFORM";
				for (Statement statement : statements) {
					perform = perform + "\n" + statement.generate();
				}
				perform = perform + "\n           END-PERFORM;";
			}
			for (Statement statement : statements) {
				perform = perform + "\n" + statement.generate();
			}
		} else {
			perform = "           PERFORM";
			for (Statement statement : statements) {
				perform = perform + "\n" + statement.generate();
			}
			perform = perform + "\n           END-PERFORM;";
		}
		return perform;
	}

}
