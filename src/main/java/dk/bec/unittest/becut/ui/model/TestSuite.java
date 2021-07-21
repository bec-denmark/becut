package dk.bec.unittest.becut.ui.model;

import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.testcase.model.BecutTestSuite;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class TestSuite extends UnitTestTreeObject {
	private ObjectProperty<BecutTestSuite> becutTestSuite = new SimpleObjectProperty<>();
	
	public TestSuite(String name, String type, String value) {
		super(name, type, value);
	}
	
	public CompileListing getCompileListing() {
		return becutTestSuite.get().getCompileListing();	
	}
	
	public ObjectProperty<BecutTestSuite> getBecutTestSuite() {
		return becutTestSuite;	
	}
	
	public void setBecutTestSuite(BecutTestSuite becutTestSuite) {
		this.becutTestSuite.set(becutTestSuite);
	}
}
