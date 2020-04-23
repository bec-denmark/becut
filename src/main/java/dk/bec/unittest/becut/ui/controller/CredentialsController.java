package dk.bec.unittest.becut.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import dk.bec.unittest.becut.Settings;
import dk.bec.unittest.becut.ftp.model.Credential;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import dk.bec.unittest.becut.ui.model.ValidationResult;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class CredentialsController extends AbstractBECutDialogController implements Initializable {
	
	@FXML
	private Label introTextLabel = new Label();
	
	SimpleStringProperty introText = new SimpleStringProperty();
	
	@FXML
	private TextField host;
	
	@FXML
	private TextField username;
	
	@FXML
	private PasswordField password;
	
	@FXML
	private CheckBox cacheCredentials;
	
	@FXML
	protected void ok() {
		ValidationResult validationResult = validate();
		if (validationResult.isSuccess()) {
			BECutAppContext appContext = BECutAppContext.getContext();
			Credential credential = new Credential(host.getText(), username.getText(), password.getText(), cacheCredentials.isSelected());
			appContext.setCredential(credential);
			closeWindow();
		}
		else {
			introText.setValue(validationResult.getInvalidText());
			introTextLabel.setTextFill(Color.RED);
		}
	}
	
	private ValidationResult validate() {
		Boolean success = Boolean.TRUE;
		String resultText = "";
		
		if (host.getText().isEmpty()) {
			success = Boolean.FALSE;
			resultText = "Missing host";
		}
		
		if (username.getText().isEmpty()) {
			success = Boolean.FALSE;
			resultText = "Missing username";
		}
		
		if (password.getText().isEmpty()) {
			success = Boolean.FALSE;
			resultText = "Missing password";
		}
		
		return new ValidationResult(success, resultText);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		host.setText(Settings.FTP_HOST);
		username.setText(Settings.USERNAME);
		password.setText(Settings.PASSWORD);
		introTextLabel.textProperty().bind(introText);
		introText.setValue("Input credentials to the mainframe");
	}

	@Override
	protected Stage getStage() {
		return (Stage) host.getScene().getWindow();
	}

}
