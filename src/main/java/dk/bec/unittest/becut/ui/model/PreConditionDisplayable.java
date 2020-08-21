package dk.bec.unittest.becut.ui.model;

public class PreConditionDisplayable extends UnitTestTreeObject {
	public PreConditionDisplayable(String preConditionType) {
		super(preConditionType, "", "");
	}

	@Override
	public void updateValue(String newValue) {
		this.setValue(newValue);
	}
}
