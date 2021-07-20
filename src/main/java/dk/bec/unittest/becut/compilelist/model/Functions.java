package dk.bec.unittest.becut.compilelist.model;

public class Functions {
	public static String parseAssignmentName(String an) {
		if (an == null) return an;
		int i = an.indexOf('-');
		if(i == -1) return an;
		int j = an.indexOf('-', i + 1);
		//label does not matter, may be skipped
		if(j == -1) return an.substring(i);
		String field = an.substring(i, j);
		switch(field) {
			//AS- field must be specified
			case "-AS" : return an.substring(j + 1);
			case "-S" : return an.substring(j + 1);
			default:
				throw new IllegalArgumentException("invalid field in assigenement name: " + an);
		}
	}
}
