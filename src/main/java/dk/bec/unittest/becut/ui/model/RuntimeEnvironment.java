package dk.bec.unittest.becut.ui.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public enum RuntimeEnvironment {
	BATCH("Batch"),
	CICS("CICS");
	
	private String runtimeName;
	
	private RuntimeEnvironment(String runtimeName) {
		this.runtimeName = runtimeName;
	}
	
	public String getRuntimeName() {
		return this.runtimeName;
	}
	
	public static ObservableList<String> getRuntimeOptions() {
		ObservableList<String> options = FXCollections.observableArrayList();
		for (RuntimeEnvironment runtimeEnvironment: RuntimeEnvironment.values()) {
			options.add(runtimeEnvironment.getRuntimeName());
		}
		return options;
	}
}
