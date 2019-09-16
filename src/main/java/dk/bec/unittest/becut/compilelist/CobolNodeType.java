package dk.bec.unittest.becut.compilelist;

public enum CobolNodeType {
	STATEMENT("statement"),
	DISPLAY_STATEMENT("displayStatement"),
	DATA_DESCRIPTION_ENTRY("dataDescriptionEntry"),
	ENTRY_NAME("entryName"),
	VALUE_CLAUSE("valueClause"),
	LITERAL("literal"),
	LITERAL_VALUE("literalValue"),
	IDENTIFIER("identifier"),
	CALL_STATEMENT("callStatement"),
	MOVE_STATEMENT("moveStatement"),
	//The sending part of a move statement
	SENDING("sending"),
	PROGRAM_NAME("programName"),
	ARG("arg"),
	LEVEL_NUMBER("levelNumber"),
	PICTURE_CLAUSE("pictureClause"),
	PICTURE_STRING("pictureString"),
	OCCURS_CLAUSE("occursClause"),
	INTEGER("integer"),
	USAGE_CLAUSE("usageClause"),
	COPY_STATEMENT("copyStatement"),
	WORKING_STORAGE("workingStorageSection"),
	LINKAGE_SECTION("linkageSection"),
	GOBACK_STATEMENT("gobackStatement"),
	DEPENDING_ON("dependingOn"),
	QUALIFIED_DATA_NAME("qualifiedDataName")
	;
	
	private final String nodeName;
	
	private CobolNodeType(String nodeName) {
		this.nodeName = nodeName;
	}
	
	@Override
	public String toString() {
		return nodeName;
	}

}
