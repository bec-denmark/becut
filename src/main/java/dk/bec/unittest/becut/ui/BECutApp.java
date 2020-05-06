package dk.bec.unittest.becut.ui;

import dk.bec.unittest.becut.ui.model.BECutAppContext;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class BECutApp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		BECutAppContext.createContext(primaryStage);
		primaryStage.setTitle("BECut - BEC unit test helper");
		BorderPane root = FXMLLoader.load(getClass().getResource("/dk/bec/unittest/becut/ui/view/BECutApp.fxml"));
		Scene scene = new Scene(root, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
