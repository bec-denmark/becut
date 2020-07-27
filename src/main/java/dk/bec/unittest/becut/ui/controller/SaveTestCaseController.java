package dk.bec.unittest.becut.ui.controller;

import java.io.File;

import dk.bec.unittest.becut.testcase.BecutTestCaseManager;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class SaveTestCaseController {
	@FXML
	private TextField testCasePath;
	
	private File testCaseFolder;

	@FXML
	private void browse() {
		DirectoryChooser chooser = new DirectoryChooser();
		testCaseFolder = chooser.showDialog(testCasePath.getScene().getWindow());
		if (testCaseFolder != null) {
			try {
				testCasePath.setText(testCaseFolder.getAbsolutePath());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	@FXML
	private void cancel() {
		testCasePath.getScene().getWindow().hide();
	}
	
	@FXML 
	void save() {
		testCaseFolder = new File(testCasePath.getText());
		BecutTestCase becutTestCase = BECutAppContext.getContext().getUnitTest().getBecutTestCase();
		BecutTestCaseManager.saveTestCase(becutTestCase, testCaseFolder.toPath());
		testCasePath.getScene().getWindow().hide();
	}
}
