package dk.bec.unittest.becut.ui.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.net.ftp.FTPClient;

import dk.bec.unittest.becut.ftp.FTPManager;
import dk.bec.unittest.becut.ftp.model.Credential;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class RetrieveFileController extends AbstractBECutDialogController {
	
	
	@FXML
	private TextField datasetName;
	
	@FXML
	private TextField localFilename;
	
	private File localFile;

	@FXML
	private void browse() {
		
		FileChooser chooser = new FileChooser();
		localFile = chooser.showOpenDialog(datasetName.getScene().getWindow());
		if (localFile != null) {
			try {
				localFilename.setText(localFile.getAbsolutePath());
			} catch (Exception e) {
				// TODO handle exception...
			}
		}
	}
	
	@FXML 
	protected void ok() {
		FTPClient ftp = new FTPClient();
		Credential credential = BECutAppContext.getContext().getCredential();
		try {
			FTPManager.connectAndLogin(ftp, credential);
			String datasetContents = FTPManager.retrieveMember(ftp, datasetName.getText());
			try (FileOutputStream fileOutputStream = new FileOutputStream(localFilename.getText());) {
				fileOutputStream.write(datasetContents.getBytes(StandardCharsets.UTF_8));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeWindow();
	}

	@Override
	protected Stage getStage() {
		return (Stage) datasetName.getScene().getWindow();
	}
	
}
