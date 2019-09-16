package dk.bec.unittest.becut.ui.model;

public class BECutAppContext {
	
	private static BECutAppContext context;
	
	private UnitTest unitTest;
	
	private BECutAppContext() {
		unitTest = new UnitTest();
	}

	public UnitTest getUnitTest() {
		return unitTest;
	}

	public static BECutAppContext getContext() {
		if (context == null) {
			context = new BECutAppContext();
		}
		return context;
	}
}
