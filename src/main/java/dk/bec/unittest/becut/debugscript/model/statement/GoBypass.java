package dk.bec.unittest.becut.debugscript.model.statement;

public class GoBypass extends StatementBase {

	@Override
	public String generate() {
		return "           GO BYPASS;";
	}

}
