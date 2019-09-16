package dk.bec.unittest.becut.testcase.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dk.bec.unittest.becut.compilelist.model.DataType;
import dk.bec.unittest.becut.compilelist.model.Record;

public class Parameter {

	private Integer level = -1;
	private String name = "";
	private DataType dataType = DataType.UNKNOWN;
	private Integer size = 0;
	private List<Parameter> subStructure = new ArrayList<>();
	private Boolean isSeventySeven = Boolean.FALSE;
	private String value = "";

	public Parameter(Record record) {
		this.level = record.getLevel();
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

	@Override
	public String toString() {
		return level.toString() + " " + name + ": {" + String.join(", ", subStructure.stream().map(Parameter::toString).collect(Collectors.toList()));
	}
}
