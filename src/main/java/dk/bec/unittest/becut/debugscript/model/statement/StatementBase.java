package dk.bec.unittest.becut.debugscript.model.statement;

public class StatementBase implements Statement {
	@Override
	public String generate() {
		return "";
	}
	
	@Override
	public String toString() {
		return generate();
	}
}
