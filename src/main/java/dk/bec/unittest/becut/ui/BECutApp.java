package dk.bec.unittest.becut.ui;

import dk.bec.unittest.becut.ui.model.BECutAppContext;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class BECutApp extends Application {
	//https://bugs.openjdk.java.net/browse/JDK-8092666
	//loader reference is needed otherwise SourceCodeController will be GCed
	FXMLLoader loader;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		BECutAppContext.createContext(primaryStage);
		primaryStage.setTitle("BECut - BEC unit test helper");
		loader = new FXMLLoader();
		loader.setLocation(getClass().getClassLoader().getResource("dk/bec/unittest/becut/ui/view/BECutApp.fxml"));
		BorderPane root = loader.load();
		Scene scene = new Scene(root, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
