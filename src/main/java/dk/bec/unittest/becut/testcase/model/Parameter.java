package dk.bec.unittest.becut.testcase.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dk.bec.unittest.becut.compilelist.model.DataType;
import dk.bec.unittest.becut.compilelist.model.Record;

public class Parameter {

	protected Integer level = -1;
	protected Integer lineNumber = 0;
	protected String name = "";
	protected DataType dataType = DataType.UNKNOWN;
	protected Integer size = 0;
	protected Boolean isSeventySeven = Boolean.FALSE;
	protected String value = "";
	protected List<Parameter> subStructure = new ArrayList<>();

	public Parameter(Record record) {
		this.level = record.getLevel();
		this.lineNumber = record.getLineNumber();
		this.name = record.getName();
		this.dataType = record.getDataType();
		this.size = record.getSize();
		for (Record r : record.getSubRecords().values()) {
			subStructure.add(new Parameter(r));
		}
		this.isSeventySeven = record.getIsSeventySeven();
	}

	public Parameter() {
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
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

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public List<Parameter> getSubStructure() {
		return subStructure;
	}

	public void setSubStructure(List<Parameter> subStructure) {
		this.subStructure = subStructure;
	}

	public Boolean getIsSeventySeven() {
		return isSeventySeven;
	}

	public void setIsSeventySeven(Boolean isSeventySeven) {
		this.isSeventySeven = isSeventySeven;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getGuiName() {
		return name;
	}
	
	public String guiString() {
		return level.toString() + " " + name;
	}

	@Override
	public String toString() {
		return level.toString() + " " + name + ": {" + String.join(", ", subStructure.stream().map(Parameter::toString).collect(Collectors.toList()));
	}
}
