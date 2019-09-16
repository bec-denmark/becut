package dk.bec.unittest.becut.ui.controller;

import java.io.File;

import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.testcase.BecutTestCaseManager;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class CreateTestCaseController {
	
	@FXML
	private TextField compileListingPath;
	
	@FXML
	private TextField testCaseName;
	
	@FXML
	private TextField testCaseID;
	
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

		CompileListing compileListing = BECutAppContext.getContext().getUnitTest().getCompileListing();
		BecutTestCase becutTestCase = BecutTestCaseManager.createTestCaseFromCompileListing(compileListing);
		if (!testCaseName.getText().trim().isEmpty()) {
			becutTestCase.setTestCaseName(testCaseName.getText());
		}
		if (!testCaseID.getText().trim().isEmpty()) {
			becutTestCase.setTestCaseId(testCaseID.getText());
		}

		BECutAppContext.getContext().getUnitTest().setBecutTestCase(becutTestCase);

		compileListingPath.getScene().getWindow().hide();
	}

	public File getCompileListingFile() {
		return compileListingFile;
	}
}
