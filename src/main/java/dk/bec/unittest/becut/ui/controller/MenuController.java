package dk.bec.unittest.becut.ui.controller;

import java.io.File;
import java.io.IOException;

import dk.bec.unittest.becut.testcase.BecutTestCaseManager;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import dk.bec.unittest.becut.ui.model.UnitTest;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MenuController extends AbstractBECutController {


	@FXML
	private MenuBar menuBar;

	@FXML
	public void createTestCase() {
		Stage loadCompileListingStage = new Stage();
		loadCompileListingStage.initModality(Modality.WINDOW_MODAL);
		loadCompileListingStage.initOwner(menuBar.getScene().getWindow());
		
		try {
			Parent parent = FXMLLoader.load(getClass().getResource("/dk/bec/unittest/becut/ui/view/CreateTestCase.fxml"));
			Scene scene = new Scene(parent, 600, 400);
			loadCompileListingStage.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		loadCompileListingStage.showAndWait();
	}

	@FXML
	public void saveAs() {
		Stage saveStage = new Stage();
		saveStage.initModality(Modality.WINDOW_MODAL);
		saveStage.initOwner(menuBar.getScene().getWindow());
		
		try {
			Parent parent = FXMLLoader.load(getClass().getResource("/dk/bec/unittest/becut/ui/view/SaveTestCase.fxml"));
			Scene scene = new Scene(parent, 500, 200);
			saveStage.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		saveStage.showAndWait();
	}

	@FXML
	public void save() {
		UnitTest unitTest = BECutAppContext.getContext().getUnitTest();
		if (unitTest.getSavePath().isEmpty()) {
			saveAs();
		}
		else {
			File file = new File(unitTest.getSavePath());
			BecutTestCaseManager.saveTestCase(unitTest.getBecutTestCase(), file);
		}
	}

	@FXML
	public void editTestCase() {
		Stage loadTestCaseStage = new Stage();
		loadTestCaseStage.initModality(Modality.WINDOW_MODAL);
		loadTestCaseStage.initOwner(menuBar.getScene().getWindow());
		
		try {
			Parent parent = FXMLLoader.load(getClass().getResource("/dk/bec/unittest/becut/ui/view/LoadTestCase.fxml"));
			Scene scene = new Scene(parent, 500, 200);
			loadTestCaseStage.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		loadTestCaseStage.showAndWait();
	}
	
	@FXML
	public void loadCompileListing() {
		Stage loadCompileListingStage = new Stage();
		loadCompileListingStage.initModality(Modality.WINDOW_MODAL);
		loadCompileListingStage.initOwner(menuBar.getScene().getWindow());
		
		try {
			Parent parent = FXMLLoader.load(getClass().getResource("/dk/bec/unittest/becut/ui/view/LoadCompileListing.fxml"));
			Scene scene = new Scene(parent, 500, 200);
			loadCompileListingStage.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		loadCompileListingStage.showAndWait();
	}

	@FXML
	public void createDebugScript() {
		Stage saveDebugScriptStage = new Stage();
		saveDebugScriptStage.initModality(Modality.WINDOW_MODAL);
		saveDebugScriptStage.initOwner(menuBar.getScene().getWindow());
		
		try {
			Parent parent = FXMLLoader.load(getClass().getResource("/dk/bec/unittest/becut/ui/view/SaveDebugScript.fxml"));
			Scene scene = new Scene(parent, 500, 200);
			saveDebugScriptStage.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		saveDebugScriptStage.showAndWait();
	}

	@FXML
	public void setupCredentials() {
		Stage credentialsStage = new Stage();
		credentialsStage.initModality(Modality.WINDOW_MODAL);
		credentialsStage.initOwner(menuBar.getScene().getWindow());
		
		try {
			Parent parent = FXMLLoader.load(getClass().getResource("/dk/bec/unittest/becut/ui/view/Credentials.fxml"));
			Scene scene = new Scene(parent, 500, 200);
			credentialsStage.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		credentialsStage.showAndWait();
	}

	@FXML
	public void retrieveFile() {
		Stage retrieveFileStage = new Stage();
		retrieveFileStage.initModality(Modality.WINDOW_MODAL);
		retrieveFileStage.initOwner(menuBar.getScene().getWindow());
		
		try {
			Parent parent = FXMLLoader.load(getClass().getResource("/dk/bec/unittest/becut/ui/view/RetrieveFile.fxml"));
			Scene scene = new Scene(parent, 500, 200);
			retrieveFileStage.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		retrieveFileStage.showAndWait();
	}
	
	@FXML
	public void runDebugScript() {
		Stage runDebugScriptStage = new Stage();
		runDebugScriptStage.initModality(Modality.WINDOW_MODAL);
		runDebugScriptStage.initOwner(menuBar.getScene().getWindow());
		
		try {
			Parent parent = FXMLLoader.load(getClass().getResource("/dk/bec/unittest/becut/ui/view/RunDebugScript.fxml"));
			Scene scene = new Scene(parent, 600, 400);
			runDebugScriptStage.setScene(scene);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} 
		runDebugScriptStage.showAndWait();
	}

	@FXML
	public void recordProgramExecution() {
		Stage recordProgramExecutionStage = new Stage();
		recordProgramExecutionStage.initModality(Modality.WINDOW_MODAL);
		recordProgramExecutionStage.initOwner(menuBar.getScene().getWindow());
		
		try {
			Parent parent = FXMLLoader.load(getClass().getResource("/dk/bec/unittest/becut/ui/view/RecordProgramExecution.fxml"));
			Scene scene = new Scene(parent, 600, 400);
			recordProgramExecutionStage.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		recordProgramExecutionStage.showAndWait();
	}

	@FXML
	public void exit() {
		closeApplication();
	}

	@Override
	protected Stage getStage() {
		return (Stage) menuBar.getScene().getWindow();
	}
}
