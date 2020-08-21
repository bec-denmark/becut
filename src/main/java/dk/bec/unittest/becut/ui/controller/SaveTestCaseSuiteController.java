package dk.bec.unittest.becut.ui.controller;

import java.io.File;

import dk.bec.unittest.becut.testcase.BecutTestCaseSuiteManager;
import dk.bec.unittest.becut.testcase.model.BecutTestCaseSuite;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

public class SaveTestCaseSuiteController {
	@FXML
	private TextField testCaseSuitePath;
	
	private File testCaseSuiteFolder;

	@FXML
	private void browse() {
		DirectoryChooser chooser = new DirectoryChooser();
		testCaseSuiteFolder = chooser.showDialog(testCaseSuitePath.getScene().getWindow());
		if (testCaseSuiteFolder != null) {
			try {
				testCaseSuitePath.setText(testCaseSuiteFolder.getAbsolutePath());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	@FXML
	private void cancel() {
		close();
	}
	
	@FXML 
	void save() {
		testCaseSuiteFolder = new File(testCaseSuitePath.getText());
		BecutTestCaseSuite becutTestCaseSuite = BECutAppContext.getContext().getUnitTestSuite().getBecutTestCaseSuite().get();
		BecutTestCaseSuiteManager.saveTestCaseSuite(becutTestCaseSuite, testCaseSuiteFolder.toPath());
		close();
	}
	
	private void close() {
		testCaseSuitePath.getScene().getWindow().hide();
	}
}
