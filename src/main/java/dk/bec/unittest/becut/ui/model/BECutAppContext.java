package dk.bec.unittest.becut.ui.model;

import java.io.IOException;

import dk.bec.unittest.becut.ftp.model.Credential;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BECutAppContext {
	
	private static BECutAppContext context;
	
	private UnitTest unitTest;
	
	private Credential credential;
	
	private Stage primaryStage;
	
	private SimpleStringProperty compileListStatus = new SimpleStringProperty("None");
	private SimpleStringProperty sourceCode = new SimpleStringProperty("None");
	
	private BECutAppContext(Stage primaryStage) {
		this.unitTest = new UnitTest();
		this.primaryStage = primaryStage;
	}

	public UnitTest getUnitTest() {
		return unitTest;
	}
	
	public Credential getCredential() {
		Credential c;
		if (credential == null) {
			//TODO Ask the user for the credential here
				Stage credentialsStage = new Stage();
				credentialsStage.initModality(Modality.WINDOW_MODAL);
				credentialsStage.initOwner(primaryStage);
				
				try {
					Parent parent = FXMLLoader.load(getClass().getResource("/dk/bec/unittest/becut/ui/view/Credentials.fxml"));
					Scene scene = new Scene(parent, 500, 200);
					credentialsStage.setScene(scene);
				} catch (IOException e) {
					e.printStackTrace();
				} 
				credentialsStage.showAndWait();
		}
		c = credential;
		if (!credential.getCache() ) {
			credential = null;
		}
		return c;
	}
	
	public void setCredential(Credential credential) {
		this.credential = credential;
	}
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public SimpleStringProperty getCompileListStatus() {
		return compileListStatus;
	}

	public SimpleStringProperty getSourceCode() {
		return sourceCode;
	}
	
	public static BECutAppContext getContext() {
		if (context == null) {
			Alert alert = new Alert(AlertType.ERROR, "No primary stage set");
			alert.showAndWait();
			Platform.exit();
		}
		return context;
	}
	
	public static void createContext(Stage primaryStage) {
		if (context == null) {
			context = new BECutAppContext(primaryStage);
		}
		else {
			Alert alert = new Alert(AlertType.ERROR, "Cannot have more than one primary stage");
			alert.showAndWait();
			Platform.exit();
		}
	}
}
