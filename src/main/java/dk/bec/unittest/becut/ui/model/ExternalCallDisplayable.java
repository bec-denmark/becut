package dk.bec.unittest.becut.ui.model;

import dk.bec.unittest.becut.testcase.model.ExternalCall;

public class ExternalCallDisplayable extends UnitTestTreeObject {

	private ExternalCall externalCall;
	
	public ExternalCallDisplayable(ExternalCall externalCall) {
		super(externalCall.getDisplayableName(), "External Call", "");
		this.externalCall = externalCall;
	}

	public ExternalCall getExternalCall() {
		return externalCall;
	}

	@Override
	public void updateValue(String newValue) {
		setValue(newValue);
	}

}
