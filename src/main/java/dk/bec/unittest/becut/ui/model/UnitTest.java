package dk.bec.unittest.becut.ui.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import dk.bec.unittest.becut.compilelist.Parse;
import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.debugscript.model.DebugScript;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class UnitTest extends UnitTestTreeObject {
	
	private CompileListing compileListing;
	private DebugScript debugScript;
	private ObjectProperty<BecutTestCase> becutTestCase = new SimpleObjectProperty<BecutTestCase>();
	private String savePath = "";
	
	public UnitTest() {
		super("Create or load a test case", "", "");
		becutTestCase.set(new BecutTestCase());
	}

	public CompileListing getCompileListing() {
		return compileListing;
	}

	public void setCompileListing(CompileListing compileListing) {
		this.compileListing = compileListing;
	}

	public void setCompileListing(File compileListingFile) {
		try {
			setCompileListing(new FileInputStream(compileListingFile));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setCompileListing(InputStream inputStream) {
		this.compileListing = Parse.parse(inputStream);
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

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	@Override
	public void updateValue(String newValue) {
		setValue(newValue);
	}
	
}
