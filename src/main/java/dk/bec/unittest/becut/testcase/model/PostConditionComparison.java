package dk.bec.unittest.becut.testcase.model;

import dk.bec.unittest.becut.recorder.model.SessionRecord;

public class PostConditionComparison {

	Parameter expected;
	SessionRecord actual;

	public PostConditionComparison(Parameter expected, SessionRecord actual) {
		this.expected = expected;
		this.actual = actual;
	}

	public boolean compare() {
		return expected.getValue().equals(actual.getValue());
	}
	
	@Override
	public String toString() {
		return actual.getName() + "\tExpected: " + expected.getValue() + "\tActual: " + actual.getValue(); 
	}
}
