package dk.bec.unittest.becut.ui.model;

public class PreConditionDisplayable extends UnitTestTreeObject {

//	private PreConditon preCondition;
	
	public PreConditionDisplayable(String preConditionType) {
		super("Precondtions", preConditionType, "");
//		this.preCondition = preCondition;
	}

//	public PreConditon getPrecondition() {
//		return preCondition;
//	}
//
//	public void setPreconditions(PreConditon preconditions) {
//		this.preconditions = preconditions;
//	}
//
	@Override
	public void updateValue(String newValue) {
		this.setValue(newValue);
	}
}
