package dk.bec.unittest.becut.ui.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import dk.bec.unittest.becut.ui.model.BECutAppContext;
import dk.bec.unittest.becut.ui.model.LoadCompileListing;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class LoadCompileListingFileController implements LoadCompileListing {

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

	@Override
	public InputStream getCompileListing() {
		try {
			return new FileInputStream(compileListingPath.getText());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void updateStatus() {
		BECutAppContext.getContext().getCompileListStatus().setValue("File: " + compileListingFile.getAbsolutePath());
	}

}
