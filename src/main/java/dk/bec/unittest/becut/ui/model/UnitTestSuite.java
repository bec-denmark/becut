package dk.bec.unittest.becut.ui.model;

import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.testcase.model.BecutTestCaseSuite;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class UnitTestSuite extends UnitTestTreeObject {
	private ObjectProperty<BecutTestCaseSuite> becutTestCaseSuite = new SimpleObjectProperty<>();
	
	public UnitTestSuite(String name, String type, String value) {
		super(name, type, value);
	}
	
	public CompileListing getCompileListing() {
		return becutTestCaseSuite.get().getCompileListing();	
	}
	
	public ObjectProperty<BecutTestCaseSuite> getBecutTestCaseSuite() {
		return becutTestCaseSuite;	
	}
	
	public void setBecutTestCaseSuite(BecutTestCaseSuite becutTestCaseSuite) {
		this.becutTestCaseSuite.set(becutTestCaseSuite);
	}
}
