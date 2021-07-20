package dk.bec.unittest.becut.ui.model;

import dk.bec.unittest.becut.testcase.model.ExternalCallIteration;

public class ExternalCallIterationDisplayable extends UnitTestTreeObject {
	ExternalCallIteration externalCallIteration;
	
	public ExternalCallIteration getExternalCallIteration() {
		return externalCallIteration;
	}

	public ExternalCallIterationDisplayable(ExternalCallIteration externalCallIteration) {
		super(externalCallIteration.getName(), "", "");
		this.externalCallIteration = externalCallIteration;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((externalCallIteration == null) ? 0 : externalCallIteration.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExternalCallIterationDisplayable other = (ExternalCallIterationDisplayable) obj;
		if (externalCallIteration == null) {
			if (other.externalCallIteration != null)
				return false;
		} else if (!externalCallIteration.equals(other.externalCallIteration))
			return false;
		return true;
	}
}
