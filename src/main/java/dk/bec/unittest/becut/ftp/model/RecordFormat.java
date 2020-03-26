package dk.bec.unittest.becut.ftp.model;

public enum RecordFormat {

	FIXED("F"),
	FIXED_BLOCK("FB"),
	VARIABLE("V"),
	VARIABLE_BLOCK("VB"),
	UNDEFINED("U");

	private String formatAbbreviation;

	private RecordFormat(String formatAbbreviation) {
		this.formatAbbreviation = formatAbbreviation;
	}

	public String getFormat() {
		return formatAbbreviation;
	}
}