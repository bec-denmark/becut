package dk.bec.unittest.becut.testcase.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PostConditionResult {
	List<PostConditionComparison> comparisons = new ArrayList<PostConditionComparison>();

	public List<PostConditionComparison> getComparisons() {
		return comparisons;
	}
	
	public Boolean isSuccess() {
		return comparisons
				.stream()
				.allMatch(PostConditionComparison::compare);
	}

	public String prettyPrint() {
		if (isSuccess()) return "Success\n";
		return "Fail\n" +
				comparisons
				.stream()
				.filter(c -> !c.compare())
				.map(PostConditionComparison::toString)
				.collect(Collectors.joining("\n", "", ""));
	}
}
