package dk.bec.unittest.becut.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import dk.bec.unittest.becut.ui.model.BECutAppContext;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class StatusBarController implements Initializable {
	@FXML
	private Label currentCompileListingLabel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		currentCompileListingLabel.textProperty().bind(BECutAppContext.getContext().getCompileListStatus());
	}
}
