package dk.bec.unittest.becut.debugscript.model;

public class LineBreakpoint implements DebugEntity, Statement {

	private Integer lineNumber;
	private Perform perform;

	public LineBreakpoint(Integer lineNumber, Perform perform) {
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
		return "           AT LINE " + lineNumber + "\n" + perform.generate();
	}

}
