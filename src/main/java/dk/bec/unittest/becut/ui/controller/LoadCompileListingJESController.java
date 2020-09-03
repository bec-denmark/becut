package dk.bec.unittest.becut.ui.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import org.apache.commons.net.ftp.FTPClient;

import dk.bec.unittest.becut.Settings;
import dk.bec.unittest.becut.ftp.FTPManager;
import dk.bec.unittest.becut.ftp.model.JESFTPDataset;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import dk.bec.unittest.becut.ui.model.LoadCompileListing;
import dk.bec.unittest.becut.ui.view.StandardAlerts;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

public class LoadCompileListingJESController implements LoadCompileListing, Initializable {
	
	@FXML
	private TextField jobNumber;

	@FXML
	private TextField stepName;

	@FXML
	private TextField DDName;

	@Override
	public InputStream getCompileListing() {
		String compileListing = "";
		FTPClient ftpClient = new FTPClient();
		try {
			Preferences pref = Preferences.userNodeForPackage(this.getClass());
			pref.put("jobNumberRemembered", jobNumber.getText());
			pref.put("stepNameRemembered", stepName.getText());
			pref.put("DDNameRemembered", DDName.getText());
			
			String job = cleanJobName();
			FTPManager.connectAndLogin(ftpClient, BECutAppContext.getContext().getCredential());
			JESFTPDataset[] datasets = FTPManager.listJES(ftpClient, job);
			String datasetID = null;
			for (JESFTPDataset dataset: datasets) {
				if (stepName.getText().equals(dataset.getStepname()) && DDName.getText().equals(dataset.getDdname())) {
					datasetID = dataset.getId().toString();
					break;
				}
				else if (DDName.getText().equals(dataset.getDdname())) {
					datasetID = dataset.getId().toString();
				}
			}
			if(datasetID == null) {
				StandardAlerts.errorDialog(String.format("(DDNAME, StepName) = (%s, %s) - no match in %s", DDName.getText(), stepName.getText(), job));
				return null;
			}
			String cl = FTPManager.retrieveJESDataset(ftpClient, job, datasetID);
			String lines[] = cl.split("\\r?\\n");
			for (int i = 0; i < lines.length; i++) {
				compileListing += lines[i].substring(1).replaceAll("\\s+$", "") + "\n";
			}
		} catch (Exception e) {
			StandardAlerts.errorDialog(e.getMessage());
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
		Preferences pref = Preferences.userNodeForPackage(this.getClass());
		jobNumber.setText(pref.get("jobNumberRemembered", ""));
		stepName.setText(pref.get("stepNameRemembered", Settings.COMPILE_STEP_NAME));
		DDName.setText(pref.get("DDNameRemembered", Settings.COMPILELIST_DD_NAME));
	}
}
