package dk.bec.unittest.becut.ui.view;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class StandardDialogs {
	
	private StandardDialogs() {}

	public static void unimplemented() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Unimplemented");
		alert.setHeaderText("Unimplemented functionality");
		alert.setContentText("The thing you are trying to do has not been implemented yet");
		alert.showAndWait();
	}

	public static void warningDialog(String title, String header, String content) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

}
