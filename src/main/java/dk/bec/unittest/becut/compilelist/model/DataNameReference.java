package dk.bec.unittest.becut.compilelist.model;

import java.util.List;

public class DataNameReference {

	private Integer lineNumber;
	private String name;
	private List<DataReference> references;

	public DataNameReference(Integer lineNumber, String name, List<DataReference> references) {
		this.lineNumber = lineNumber;
		this.name = name;
		this.references = references;
	}

	public Integer getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DataReference> getReferences() {
		return references;
	}

	public void setReferences(List<DataReference> references) {
		this.references = references;
	}

	public static class DataReference {
		private Integer lineNumber;
		private Boolean modifiable;

		public DataReference(Integer lineNumber, Boolean modifiable) {
			super();
			this.lineNumber = lineNumber;
			this.modifiable = modifiable;
		}

		public Integer getLineNumber() {
			return lineNumber;
		}

		public void setLineNumber(Integer lineNumber) {
			this.lineNumber = lineNumber;
		}

		public Boolean getModifiable() {
			return modifiable;
		}

		public void setModifiable(Boolean modifiable) {
			this.modifiable = modifiable;
		}
	}
}
