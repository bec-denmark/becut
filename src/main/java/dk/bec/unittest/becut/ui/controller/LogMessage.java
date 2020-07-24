package dk.bec.unittest.becut.ui.controller;

public class LogMessage extends RuntimeException {
	String content;
	
	LogMessage(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "SYSOUT\n" + content;
	}
	
	//performance reasons
	@Override
	public Throwable fillInStackTrace() {
		return null;
	}
}
