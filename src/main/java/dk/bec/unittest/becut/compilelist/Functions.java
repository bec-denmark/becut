package dk.bec.unittest.becut.compilelist;

import dk.bec.unittest.becut.Constants;
import dk.bec.unittest.becut.compilelist.model.CompileListing;
import koopa.core.trees.Tree;
import koopa.core.trees.jaxen.Jaxen;

public class Functions {
	static public boolean hasDB2Calls(Tree ast) {
		return Jaxen.evaluate(ast, "*//programName//text()")
			.stream()
			.map(Tree.class::cast)
			.map(t -> stripQuotes(t.getText()))
			.filter(Constants.IBMHostVariableMemoryAllocationPrograms::contains)
			.findFirst()
			.isPresent();
	}

	static public boolean compiledWithCoprocessor(CompileListing listing) {
		return listing.getCompileOptions().getOptions().containsKey("SQL(DB2)");
	}
	
	public static String stripQuotes(String s) {
		if(s == null || s.isEmpty()) return s;
		char first = s.charAt(0);
		char last = s.charAt(s.length() - 1);
		int start = (first == '\'' || first == '"') ? 1 : 0;
		int end = (last == '\'' || last == '"') ? s.length() - 1 : s.length();
		return s.substring(start, end);
	}
	
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
