package dk.bec.unittest.becut.debugscript.model;

public class ProgramTerminationBreakpoint implements DebugEntity {
	
	private Perform perform;

	public ProgramTerminationBreakpoint(Perform perform) {
		this.perform = perform;
	}
	@Override
	public String generate() {
		return "           AT TERMINATION " + perform.generate();
	}

}
