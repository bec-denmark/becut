package dk.bec.unittest.becut.ui.controller;

public class ReturnCodeDifferentFromCC000 extends RuntimeException {
	String content;
	
	public ReturnCodeDifferentFromCC000(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "SYSOUT\n" + content;
	}
}
