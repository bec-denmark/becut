package dk.bec.unittest.becut.ui.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.net.ftp.FTPClient;

import dk.bec.unittest.becut.Settings;
import dk.bec.unittest.becut.ftp.FTPManager;
import dk.bec.unittest.becut.ftp.model.JESFTPDataset;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import dk.bec.unittest.becut.ui.model.LoadCompileListing;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

public class LoadCompileListingJESController implements LoadCompileListing, Initializable {
	
	@FXML
	private TextField jobNumber;
	private static String jobNumberRemembered;

	@FXML
	private TextField stepName;
	private static String stepNameRemembered;

	@FXML
	private TextField DDName;
	private static String DDNameRemembered;

	@Override
	public InputStream getCompileListing() {
		String compileListing = "";
		FTPClient ftpClient = new FTPClient();
		try {
			jobNumberRemembered = jobNumber.getText();
			stepNameRemembered = stepName.getText();
			DDNameRemembered = DDName.getText();
			String job = cleanJobName();
			FTPManager.connectAndLogin(ftpClient, BECutAppContext.getContext().getCredential());
			JESFTPDataset[] datasets = FTPManager.listJES(ftpClient, job);
			String datasetID = "";
			for (JESFTPDataset dataset: datasets) {
				if (stepName.getText().equals(dataset.getStepname()) && DDName.getText().equals(dataset.getDdname())) {
					datasetID = dataset.getId().toString();
					break;
				}
				else if (DDName.getText().equals(dataset.getDdname())) {
					datasetID = dataset.getId().toString();
				}
			}
			String cl = "";
			cl = FTPManager.retrieveJESDataset(ftpClient, job, datasetID);
			String lines[] = cl.split("\\r?\\n");
			for (int i = 0; i < lines.length; i++) {
				compileListing += lines[i].substring(1).replaceAll("\\s+$", "") + "\n";
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new ByteArrayInputStream(compileListing.getBytes());
	}
	
	private String cleanJobName() {
		String job = jobNumber.getText();
		if (job.matches("\\d{5}")) {
			job = "JOB" + job;
		}
		return job;
	}

	@Override
	public void updateStatus() {
		BECutAppContext.getContext().getCompileListStatus().setValue("JES: " + cleanJobName());
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if(jobNumberRemembered != null) {
			jobNumber.setText(jobNumberRemembered);
		}
		if(stepNameRemembered != null) {
			stepName.setText(stepNameRemembered);
		} else {
			stepName.setText(Settings.COMPILE_STEP_NAME);	
		}
		if(DDNameRemembered != null) {
			DDName.setText(DDNameRemembered);
		} else {
			DDName.setText(Settings.COMPILELIST_DD_NAME);
		}
	}
}
