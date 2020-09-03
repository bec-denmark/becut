package dk.bec.unittest.becut.debugscript.model.statement;

import dk.bec.unittest.becut.debugscript.model.DebugEntity;
import dk.bec.unittest.becut.debugscript.model.Perform;

public class AtCallStarBreakpoint extends StatementBase implements DebugEntity {
	private Perform perform;

	public AtCallStarBreakpoint(Perform perform) {
		this.perform = perform;
	}

	public Perform getPerform() {
		return perform;
	}

	public void setPerform(Perform perform) {
		this.perform = perform;
	}

	@Override
	public String generate() {
		return "           AT CALL *\n" + perform.generate();
	}
}
