package dk.bec.unittest.becut.debugscript.model;

public class Breakpoint implements DebugEntity {

	private Integer lineNumber;
	private Perform perform;

	public Breakpoint(Integer lineNumber, Perform perform) {
		super();
		this.lineNumber = lineNumber;
		this.perform = perform;
	}

	public Integer getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	public Perform getPerform() {
		return perform;
	}

	public void setPerform(Perform perform) {
		this.perform = perform;
	}

	@Override
	public String generate() {
		if (this.lineNumber == 0) {
			return "           AT ENTRY PROCEDURE DIVISION" + "\n " + perform.generate();
		}
		return "           AT LINE " + lineNumber + "\n" + perform.generate();
	}

}
