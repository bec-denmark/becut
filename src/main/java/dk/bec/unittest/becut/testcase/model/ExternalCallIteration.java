package dk.bec.unittest.becut.testcase.model;

import java.util.List;

public class ExternalCallIteration {

	private Integer numericalOrder;
	private String name;
	private List<Parameter> parameters;
	
	

	public ExternalCallIteration(Integer numericalOrder, String name, List<Parameter> parameters) {
		this.numericalOrder = numericalOrder;
		this.name = name;
		this.parameters = parameters;
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

}
