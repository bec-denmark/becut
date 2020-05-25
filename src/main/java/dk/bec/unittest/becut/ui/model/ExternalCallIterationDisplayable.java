package dk.bec.unittest.becut.ui.model;

import dk.bec.unittest.becut.testcase.model.ExternalCallIteration;

public class ExternalCallIterationDisplayable extends UnitTestTreeObject {

	ExternalCallIteration externalCallIteration;
	
	public ExternalCallIterationDisplayable(ExternalCallIteration externalCallIteration) {
		super(externalCallIteration.getName(), "", "");
		this.externalCallIteration = externalCallIteration;
	}

}
