package dk.bec.unittest.becut.ui.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTPClient;

import dk.bec.unittest.becut.ftp.FTPManager;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import dk.bec.unittest.becut.ui.model.LoadCompileListing;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoadCompileListingDatasetController implements LoadCompileListing {
	
	@FXML
	private TextField datasetName;

	@Override
	public InputStream getCompileListing() {
		String compileListing = "";
		FTPClient ftpClient = new FTPClient();
		try {
			FTPManager.connectAndLogin(ftpClient, BECutAppContext.getContext().getCredential());
			compileListing = FTPManager.retrieveMember(ftpClient, datasetName.getText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ByteArrayInputStream(compileListing.getBytes());
	}

	@Override
	public void updateStatus() {
		BECutAppContext.getContext().getCompileListStatus().setValue("Dataset: " + datasetName.getText());
	}
}
