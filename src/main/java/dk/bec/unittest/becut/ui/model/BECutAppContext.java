package dk.bec.unittest.becut.ui.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.ftp.model.Credential;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import koopa.core.trees.Tree;

public class BECutAppContext {
	private static BECutAppContext context;
	
	private TestSuite testSuite;
	
	private Path testSuiteFolder;
	
	private Credential credential;
	
	private Stage primaryStage;
	
	private SimpleStringProperty compileListStatus = new SimpleStringProperty("None");
	private SimpleObjectProperty<List<String>> sourceCode = new SimpleObjectProperty<>();
	
	EventBus eventBus = new EventBus(); 
	
	public EventBus getEventBus() {
		return eventBus;
	}

	private BECutAppContext(Stage primaryStage) {
		this.testSuite = new TestSuite("Test suite", "", "");
		this.primaryStage = primaryStage;
	}

	public TestSuite getUnitTestSuite() {
		return testSuite;
	}
	
	public Tree getAst() {
		return testSuite.getCompileListing().getSourceMapAndCrossReference().getAst();
	}
	
	public CompileListing getCompileListing() {
		return testSuite.getCompileListing();
	}
	
	public Path getUnitTestSuiteFolder() {
		if(testSuiteFolder == null || !Files.exists(testSuiteFolder)) {
			try {
				testSuiteFolder = Files.createTempDirectory("becut");
				return testSuiteFolder;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return testSuiteFolder;
	}

	public Path getTestScriptPath() {
		return Paths.get(getUnitTestSuiteFolder().toString(), "test_script.txt");
	}

	public Path getRecordScriptPath() {
		return Paths.get(getUnitTestSuiteFolder().toString(), "record_script.txt");
	}
	
	public void setTestSuiteFolder(Path testSuiteFolder) {
		this.testSuiteFolder = testSuiteFolder;
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
