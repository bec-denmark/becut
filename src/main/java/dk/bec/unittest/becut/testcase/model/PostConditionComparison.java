package dk.bec.unittest.becut.testcase.model;

import java.math.BigDecimal;

import dk.bec.unittest.becut.compilelist.model.DataType;
import dk.bec.unittest.becut.recorder.model.SessionRecord;

public class PostConditionComparison {

	Parameter expected;
	SessionRecord actual;

	public PostConditionComparison(Parameter expected, SessionRecord actual) {
		this.expected = expected;
		this.actual = actual;
	}

	public boolean compare() {
		if((actual.getValue() == null && expected.getValue() != null)
			|| (actual.getValue() != null && expected.getValue() == null)) {
			return false;
		}
		// for numeric and alphanumeric ignore leading zeroes, whitespaces
		else if (expected.dataType.equals(DataType.PIC_NUMERIC)) {
			try {
				return new BigDecimal(expected.getValue()).equals(new BigDecimal(actual.getValue()));
			} catch(NumberFormatException e) {
				System.err.println(e.getMessage());
				return false;
			}
		} else if (expected.dataType.equals(DataType.PIC)) {
			return expected.getValue().trim().equals(actual.getValue().trim());
		}
		return expected.getValue().equals(actual.getValue());
	}
	
	@Override
	public String toString() {
		return actual.getName() + "\tExpected: " + expected.getValue() + "\tActual: " + actual.getValue();
	}
}
