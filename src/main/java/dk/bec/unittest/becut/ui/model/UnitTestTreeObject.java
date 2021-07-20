package dk.bec.unittest.becut.ui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UnitTestTreeObject {
	
	private final StringProperty name = new SimpleStringProperty();
	private final StringProperty type = new SimpleStringProperty();
	private final StringProperty value = new SimpleStringProperty();
	
	public UnitTestTreeObject(String name) {
		this(name, "", "");
	}
	
	public UnitTestTreeObject(String name, String type, String value) {
		setName(name);
		setType(type);
		setValue(value);
	}
	
	public final StringProperty nameProperty() {
		return name;
	}
	
	public final String getName() {
		return this.nameProperty().get();
	}
	
	public final void setName(String name) {
		this.nameProperty().set(name);
	}

	public final StringProperty typeProperty() {
		return type;
	}

	public final String getType() {
		return this.typeProperty().get();
	}
	
	public final void setType(String type) {
		this.typeProperty().set(type);
	}

	public final StringProperty valueProperty() {
		return value;
	}

	public final String getValue() {
		return this.valueProperty().get();
	}
	
	public final void setValue(String value) {
		this.valueProperty().set(value);
	}
	
	public void updateValue(String newValue) {
		this.setValue(newValue);
	}
}
