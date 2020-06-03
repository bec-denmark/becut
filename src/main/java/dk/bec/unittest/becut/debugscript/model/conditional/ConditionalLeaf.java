package dk.bec.unittest.becut.debugscript.model.conditional;

public class ConditionalLeaf implements Conditional {
	
	private String value;
	
	public ConditionalLeaf(String value) {
		this.value = value;
	}

	@Override
	public String generate() {
		return value;
	}
	

}
