package dk.bec.unittest.becut.ui.model;

import dk.bec.unittest.becut.testcase.model.Parameter;

public class ParameterDisplayable extends UnitTestTreeObject {
	
	private Parameter parameter;
	
	public ParameterDisplayable(Parameter parameter) {
		super(parameter.guiString(), parameter.getDataType().toString(), parameter.getValue());
		this.parameter = parameter;
	}

	public Parameter getParameter() {
		return parameter;
	}

	@Override
	public void updateValue(String newValue) {
		setValue(newValue);
		parameter.setValue(newValue);
	}

}
