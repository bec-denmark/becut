package dk.bec.unittest.becut.debugscript.model;

public class Goto implements Statement {

	private Integer lineNumber;

	public Goto(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	public Integer getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	@Override
	public String generate() {
		return "           GOTO " + lineNumber + ";";
	}

}
