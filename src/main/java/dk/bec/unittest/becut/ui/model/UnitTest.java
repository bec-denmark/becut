package dk.bec.unittest.becut.ui.model;

import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class UnitTest extends UnitTestTreeObject {
	private ObjectProperty<BecutTestCase> becutTestCase = new SimpleObjectProperty<BecutTestCase>();
	
	public UnitTest(BecutTestCase becutTestCase) {
		super("TestCase", "", becutTestCase.getTestCaseName());
		this.becutTestCase.set(becutTestCase);
	}

	public final BecutTestCase getBecutTestCase() {
		return becutTestCase.get();
	}

	public final void setBecutTestCase(BecutTestCase becutTestCase) {
		this.becutTestCase.set(becutTestCase);
	}
	
	public ObjectProperty<BecutTestCase> becutTestCaseProperty() {
		return becutTestCase;
	}

	@Override
	public void updateValue(String newValue) {
		setValue(newValue);
	}
}
