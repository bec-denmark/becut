package dk.bec.unittest.becut.ui.controller;

import java.io.File;

import dk.bec.unittest.becut.ui.model.BECutAppContext;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class LoadCompileListingController {

	@FXML
	private TextField compileListingPath;
	
	private File compileListingFile;

	@FXML
	private void browse() {
		
		FileChooser chooser = new FileChooser();
		compileListingFile = chooser.showOpenDialog(compileListingPath.getScene().getWindow());
		if (compileListingFile != null) {
			try {
				compileListingPath.setText(compileListingFile.getAbsolutePath());
			} catch (Exception e) {
				// handle exception...
			}
		}
	}
	
	@FXML
	private void cancel() {
		compileListingPath.getScene().getWindow().hide();
	}
	
	@FXML 
	void ok() {
		compileListingFile = new File(compileListingPath.getText());
		BECutAppContext.getContext().getUnitTest().setCompileListing(compileListingFile);
		compileListingPath.getScene().getWindow().hide();
	}

	public File getCompileListingFile() {
		return compileListingFile;
	}

}
