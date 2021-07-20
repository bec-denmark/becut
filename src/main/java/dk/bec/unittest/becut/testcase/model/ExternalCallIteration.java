package dk.bec.unittest.becut.testcase.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ExternalCallIteration {
	private Integer numericalOrder;
	private String name;

	private Boolean isDefault = Boolean.FALSE;
	private List<Parameter> parameters;

	public ExternalCallIteration(Integer numericalOrder, String name, List<Parameter> parameters, Boolean isDefault) {
		this.numericalOrder = numericalOrder;
		this.name = name;
		this.parameters = parameters;
		this.isDefault = isDefault;
	}

	public ExternalCallIteration(Integer numericalOrder, String name, List<Parameter> parameters) {
		this.numericalOrder = numericalOrder;
		this.name = name;
		this.parameters = parameters;
	}
	
	public ExternalCallIteration() {}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	public void setNumericalOrder(Integer numericalOrder) {
		this.numericalOrder = numericalOrder;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getNumericalOrder() {
		return numericalOrder;
	}

	public String getName() {
		return name;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}
	
	public Boolean hasValues() {
		Boolean v = Boolean.FALSE;
		for (Parameter p: parameters) {
			v = v || p.hasValues();
		}
		return v;
	}

	public Boolean isDefault() {
		return isDefault;
	}

	public void setDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	public static ExternalCallIteration mkCopy(ExternalCallIteration externalCallIteration) {
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayOutputStream os = new ByteArrayOutputStream(); 
		try {
			mapper.writer().writeValue(os, externalCallIteration);
			return mapper.readValue(new ByteArrayInputStream(os.toByteArray()), ExternalCallIteration.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean equals(Object that) {
		return that instanceof ExternalCallIteration && equals((ExternalCallIteration) that);
	}

	public boolean equals(ExternalCallIteration that) {
		return this.name == that.getName()
				&& this.parameters.equals(that.parameters);
	}
}
