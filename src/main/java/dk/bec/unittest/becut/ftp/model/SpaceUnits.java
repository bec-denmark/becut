package dk.bec.unittest.becut.ftp.model;

public enum SpaceUnits {

	BLOCKS("BLKS"),
	TRACKS("TRKS"),
	CYLINDERS("CYLS"),
	NOT_SPECIFIED("");
	
	private String spaceAbbreviation;
	
	private SpaceUnits(String spaceAbbreviation) {
		this.spaceAbbreviation = spaceAbbreviation;
	}
	
	public String getSpace() {
		return spaceAbbreviation;
	}
}
