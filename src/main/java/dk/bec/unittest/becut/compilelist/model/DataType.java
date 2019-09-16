package dk.bec.unittest.becut.compilelist.model;


public enum DataType {
	
	PIC,
	PIC_NUMERIC,
	INDEX,
	BINARY,
	PACKED_DECIMAL,
	GROUP,
	NONE,
	UNKNOWN,
	POINTER,
	EIGHTYEIGHT,
	CEE_Entry	//procedure pointer
	;
	
	public static DataType parseDataType(String dataType) {
		
		switch (dataType.toLowerCase()) {
		case "display": return PIC;
		case "group": return GROUP;
		case "packed-dec": return PACKED_DECIMAL;
		case "binary": return BINARY;
		case "disp-num": return PIC_NUMERIC;
		}
		
		return UNKNOWN;
	}

}
