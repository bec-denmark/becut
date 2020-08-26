package dk.bec.unittest.becut.testcase.model;

public class TestResult {
	public TestResult(BecutTestCase becutTestCase, PostConditionResult postConditionResult) {
		status = postConditionResult.isSuccess() ? TestResultStatus.OK : TestResultStatus.NOK;
		message = postConditionResult.prettyPrint();
		testCase = becutTestCase;
	}
	
	public BecutTestCase getTestCase() {
		return testCase;
	}
	public TestResultStatus getStatus() {
		return status;
	}
	public String getMessage() {
		return message;
	}

	private BecutTestCase testCase;
	private TestResultStatus status;
	private String message;
}
