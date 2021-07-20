package dk.bec.unittest.becut.ui.model;

public class FileControlDisplayable extends UnitTestTreeObject {
	public FileControlDisplayable(String fileControlAssignment, String path) {
		super(fileControlAssignment, "", path);
	}

	@Override
	public void updateValue(String newValue) {
		setValue(newValue);
	}
}
