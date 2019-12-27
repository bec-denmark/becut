package dk.bec.unittest.becut.debugscript.model;

public class ProgramStartBreakpoint implements DebugEntity {

	private Perform perform;

	public ProgramStartBreakpoint(Perform perform) {
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
		return perform.generate();
	}

}
