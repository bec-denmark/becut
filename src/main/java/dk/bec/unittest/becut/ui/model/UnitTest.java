package dk.bec.unittest.becut.ui.model;

import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.debugscript.model.DebugScript;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class UnitTest extends UnitTestTreeObject {
	private DebugScript debugScript;
	private ObjectProperty<BecutTestCase> becutTestCase = new SimpleObjectProperty<BecutTestCase>();
	
	public UnitTest() {
		super("Create or load a test case", "", "");
		becutTestCase.set(new BecutTestCase());
	}

	public CompileListing getCompileListing() {
		return getBecutTestCase().getCompileListing();
	}

	public void setCompileListing(CompileListing compileListing) {
		getBecutTestCase().setCompileListing(compileListing);
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

	public DebugScript getDebugScript() {
		return debugScript;
	}

	public void setDebugScript(DebugScript debugScript) {
		this.debugScript = debugScript;
	}

	@Override
	public void updateValue(String newValue) {
		setValue(newValue);
	}
}
