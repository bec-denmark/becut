package dk.bec.unittest.becut.compilelist.model;

import java.util.List;

public abstract class AbstractCompileListingSection {

	protected List<String> originalSource;

	public List<String> getOriginalSource() {
		return originalSource;
	}

	public void setOriginalSource(List<String> originalSource) {
		this.originalSource = originalSource;
	}

}
