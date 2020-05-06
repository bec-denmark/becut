package dk.bec.unittest.becut.ui.controller;

import java.io.File;

import dk.bec.unittest.becut.testcase.BecutTestCaseManager;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class LoadTestCaseController {

	@FXML
	private TextField testCasePath;
	
	private File testCaseFile;

	@FXML
	private void browse() {
		
		FileChooser chooser = new FileChooser();
		testCaseFile = chooser.showOpenDialog(testCasePath.getScene().getWindow());
		if (testCaseFile != null) {
			try {
				testCasePath.setText(testCaseFile.getAbsolutePath());
			} catch (Exception e) {
				// handle exception...
			}
		}
	}
	
	@FXML
	private void cancel() {
		testCasePath.getScene().getWindow().hide();
	}
	
	@FXML 
	private void ok() {
		testCaseFile = new File(testCasePath.getText());

		BecutTestCase becutTestCase = BecutTestCaseManager.loadTestCase(testCaseFile);
		BECutAppContext.getContext().getUnitTest().setBecutTestCase(becutTestCase);

		testCasePath.getScene().getWindow().hide();
	}

}
