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

public class MenuController {


	@FXML
	private MenuBar menuBar;

	@FXML
	public void createTestCase() {
		Stage loadCompileListingStage = new Stage();
		loadCompileListingStage.initModality(Modality.WINDOW_MODAL);
		loadCompileListingStage.initOwner(menuBar.getScene().getWindow());
		
		try {
			Parent parent = FXMLLoader.load(getClass().getResource("/dk/bec/unittest/becut/ui/view/CreateTestCase.fxml"));
			Scene scene = new Scene(parent, 500, 200);
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
	public void exit() {
		menuBar.getScene().getWindow().hide();
	}
}
