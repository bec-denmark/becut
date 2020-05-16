package dk.bec.unittest.becut.testcase.model;

import java.util.ArrayList;
import java.util.List;

public class PostConditionResult {
	
	List<PostConditionComparison> comparisons = new ArrayList<PostConditionComparison>();

	public List<PostConditionComparison> getComparisons() {
		return comparisons;
	}
	
	public Boolean isSuccess() {
		Boolean success = Boolean.TRUE;
		for (PostConditionComparison comparison: comparisons) {
			success = success && comparison.compare();
		}
		return success;
	}

	public String prettyPrint() {
		String result = "";
		if (isSuccess()) {
			result += "Success\n";
		}
		else {
			result += "Fail\n";
		}
		result += "\n";
		result += "\n";
		for (PostConditionComparison comparison: comparisons) {
			result += comparison.toString() + "\n";
		}
		return result;
	}
	
	

}
