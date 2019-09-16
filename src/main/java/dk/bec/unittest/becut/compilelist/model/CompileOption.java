package dk.bec.unittest.becut.compilelist.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompileOption {
	
	private String name;
	private List<String> options;
	
	public CompileOption(String optionText) {
		String ot = optionText.trim();
		if (ot.contains("(")) {
			int start = ot.indexOf("(");
			name = ot.substring(0, start);
			options = Arrays.asList(ot.substring(start + 1, ot.length()-1).split(","));
			
		} else {
			name = ot;
			options = new ArrayList<String>();
		}
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	@Override
	public String toString() {
		return "CompileOption [name=" + name + ", options=" + options + "]";
	}

}
