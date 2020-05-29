package dk.bec.unittest.becut.debugscript.model.variable;

public class Pic9Comp extends Variable {

	private Integer length;

	public Pic9Comp(String name, Integer length) {
		this.name = name;
		this.length = length;
		this.type = "PIC 9";
	}

	@Override
	public String generate() {
		return name;
	}
	
	public String declaration() {
		return "        01 " + name + " PIC 9(" + length + ") COMP;\n";
	}
}
