package dk.bec.unittest.becut.ui.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.prefs.Preferences;

import dk.bec.unittest.becut.Either;
import dk.bec.unittest.becut.testcase.BecutTestCaseSuiteManager;
import dk.bec.unittest.becut.testcase.model.BecutTestSuite;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

public class LoadTestCaseSuiteController {
	@FXML
	private TextField testCasePath;
	
	private File testCaseSuiteFolder;
	
	static Path initialDirectory;

	@FXML
	private void browse() {
		Preferences pref = Preferences.userNodeForPackage(this.getClass());
		Path initialDirectory = Paths.get(pref.get("initialDirectory", System.getProperty("user.home")));

		DirectoryChooser chooser = new DirectoryChooser();
		if(initialDirectory != null && Files.exists(initialDirectory)) {
			chooser.setInitialDirectory(initialDirectory.getParent().toFile());
		}
		testCaseSuiteFolder = chooser.showDialog(testCasePath.getScene().getWindow());
		if (testCaseSuiteFolder != null) {
			try {
				testCasePath.setText(testCaseSuiteFolder.getAbsolutePath());
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
	private void ok() {
		Either<BecutTestSuite, String> becutTestCaseSuite = BecutTestCaseSuiteManager.loadTestCaseSuite(testCaseSuiteFolder.toPath());
		becutTestCaseSuite.apply(testSuite -> {
				Preferences pref = Preferences.userNodeForPackage(this.getClass());
				pref.put("initialDirectory", testCaseSuiteFolder.toPath().toString());
				BECutAppContext.getContext().setTestSuiteFolder(testCaseSuiteFolder.toPath());
				BECutAppContext.getContext().getUnitTestSuite().setBecutTestSuite(testSuite);
				
				List<String> lines = testSuite.getCompileListing().getSourceMapAndCrossReference().getOriginalSource();
				BECutAppContext.getContext().getSourceCode().setValue(lines);
			}, 
			errors -> {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Warning Dialog");
				alert.setContentText(errors.toString());
				alert.showAndWait();
			});
		close();
	}
	
	private void close() {
		testCasePath.getScene().getWindow().hide();
	}
}
