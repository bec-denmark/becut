package dk.bec.unittest.becut.testcase.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	
	@JsonIgnore
	public String getGuiName() {
		return name;
	}
	
	public String guiString() {
		return level.toString() + " " + name;
	}
	
	public Boolean hasValues() {
		Boolean v = Boolean.FALSE;
		if (!value.equals("")) {
			v = Boolean.TRUE;
		}
		for (Parameter p: subStructure) {
			v = v || p.hasValues();
		}
		return v;
	}
	
	public Parameter copyWithNoValues() {
		Parameter parameter = new Parameter();
		parameter.setLevel(level);
		parameter.setLineNumber(lineNumber);
		parameter.setName(name);
		parameter.setDataType(dataType);
		parameter.setSize(size);
		parameter.setIsSeventySeven(isSeventySeven);
		for (Parameter p: subStructure) {
			parameter.getSubStructure().add(p.copyWithNoValues());
		}
		return parameter;
	}

	@Override
	public String toString() {
		return level.toString() + " " + name + ": {" + String.join(", ", 
				subStructure.stream()
					.map(Parameter::toString)
					.collect(Collectors.toList()));
	}
	
	@Override
	public boolean equals(Object that) {
		return (that instanceof Parameter) && equals0((Parameter)that);
	}

	private boolean equals0(Parameter that) {
		return this.level == that.level 
				&& this.lineNumber == that.lineNumber
				&& this.name == that.name
				&& this.dataType == that.dataType
				&& this.size == that.size
				&& this.value == that.value
				&& subStructure.equals(that.subStructure);
	}
	
	public boolean matches(String s) {
		return name.equals(s);
	}
}
