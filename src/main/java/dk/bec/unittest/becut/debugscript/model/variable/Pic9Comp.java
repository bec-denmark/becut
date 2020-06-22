package dk.bec.unittest.becut.debugscript.model.variable;

public class Pic9Comp extends Variable {

	private Integer length;

	public Pic9Comp(String name, Integer length, String defaultValue) {
		this.name = name;
		this.length = length;
		this.type = "PIC 9";
		this.defaultValue = defaultValue;
	}

	@Override
	public String generate() {
		return name;
	}
	
	@Override
	public String declaration() {
		String result = "        01 " + name + " PIC 9(" + length + ") COMP;\n"; 
		if (!defaultValue.isEmpty() ) {
			result += "        MOVE " + defaultValue + " TO " + name + ";\n";
		}
		return result;
	}
}
