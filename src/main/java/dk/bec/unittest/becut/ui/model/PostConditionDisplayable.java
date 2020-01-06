package dk.bec.unittest.becut.ui.model;

public class PostConditionDisplayable extends UnitTestTreeObject {

	public PostConditionDisplayable(String postConditionType) {
		super(postConditionType, "", "");
	}

	@Override
	public void updateValue(String newValue) {
		setValue(newValue);
	}
}
