package dk.bec.unittest.becut.testcase.model;

import java.util.ArrayList;

import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.debugscript.model.DebugScript;

public class BecutTestCaseSuite extends ArrayList<BecutTestCase> {
	public BecutTestCaseSuite() {
	}
	
	private CompileListing compileListing;
	private DebugScript debugScript;
	
	public CompileListing getCompileListing() {
		return compileListing;
	}

	public void setCompileListing(CompileListing compileListing) {
		this.compileListing = compileListing;
	}
	
	public DebugScript getDebugScript() {
		return debugScript;
	}

	public void setDebugScript(DebugScript debugScript) {
		this.debugScript = debugScript;
	}
}
