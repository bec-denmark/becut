package dk.bec.unittest.becut.compilelist.model;

import java.util.ArrayList;
import java.util.List;

public class ProgramReference {
	private String programType = "";
	private String programName = "";
	private List<Integer> references = new ArrayList<Integer>();
	
	public ProgramReference(String programType, String programName, List<Integer> references) {
		this.programType = programType;
		this.programName = programName;
		this.references = references;
	}

	public String getProgramType() {
		return programType;
	}

	public void setProgramType(String programType) {
		this.programType = programType;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public List<Integer> getReferences() {
		return references;
	}

	public void setReferences(List<Integer> references) {
		this.references = references;
	}

	@Override
	public String toString() {
		return "ProgramReference [programType=" + programType + ", programName=" + programName + ", references="
				+ references + "]";
	}

}
