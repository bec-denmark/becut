package dk.bec.unittest.becut.ui.controller;

public class MissingINSPLOGException extends RuntimeException {
	String content;
	
	MissingINSPLOGException(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "JESYSMSG \n" + content;
	}
}
