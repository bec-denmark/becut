package dk.bec.unittest.becut.ui.controller;

public class SourceLineEvent {
	public SourceLineEvent(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	public Integer getLineNumber() {
		return lineNumber;
	}

	private Integer lineNumber;
}
