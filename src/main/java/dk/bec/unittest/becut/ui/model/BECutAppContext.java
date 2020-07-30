package dk.bec.unittest.becut.ui.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import dk.bec.unittest.becut.ftp.model.Credential;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
	
	private Path unitTestFolder;
	
	private Credential credential;
	
	private Stage primaryStage;
	
	private SimpleStringProperty compileListStatus = new SimpleStringProperty("None");
	private SimpleObjectProperty<List<String>> sourceCode = new SimpleObjectProperty<>();
	
	private ObservableList<Integer> queue = FXCollections.observableList(new ArrayList<>());
	
	public ObservableList<Integer> getQueue() {
		return queue;
	}

	private BECutAppContext(Stage primaryStage) {
		this.unitTest = new UnitTest();
		this.primaryStage = primaryStage;
	}

	public UnitTest getUnitTest() {
		return unitTest;
	}

	public Path getUnitTestFolder() {
		if(unitTestFolder == null || !Files.exists(unitTestFolder)) {
			try {
				unitTestFolder = Files.createTempDirectory("becut");
				return unitTestFolder;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return unitTestFolder;
	}

	public Path getDebugScriptPath() {
		return Paths.get(getUnitTestFolder().toString(), "debug_script.txt");
	}
	
	public void setUnitTestFolder(Path unitTestFolder) {
		this.unitTestFolder = unitTestFolder;
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

	public ObjectProperty<List<String>> getSourceCode() {
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
