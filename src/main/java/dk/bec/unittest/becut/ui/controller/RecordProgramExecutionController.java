package dk.bec.unittest.becut.ui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import dk.bec.unittest.becut.recorder.RecorderManager;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import dk.bec.unittest.becut.ui.model.RuntimeEnvironment;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class RecordProgramExecutionController extends AbstractBECutController implements Initializable {

	@FXML
	private TextField programName;
	static String programNameRemembered; 
	
	@FXML
	private ComboBox<String> runtimeEnviromentsBox;

	@FXML
	private TextField jobName;
	static String jobNameRemembered;

	@FXML
	private Pane compileListingPane;

	private Node compileListingNode;

	private LoadCompileListingController compileListingController;

	@FXML
	private void cancel() {
		programName.getScene().getWindow().hide();
	}

	@FXML
	protected void ok() {
		compileListingController.loadCompileListingIntoContext();
		try {
			Preferences pref = Preferences.userNodeForPackage(this.getClass());
			pref.put("jobNameRemembered", jobName.getText());
			pref.put("programNameRemembered", programName.getText());
			
			BecutTestCase becutTestCase = RecorderManager.recordBatch(BECutAppContext.getContext(),
					jobName.getText(), BECutAppContext.getContext().getUnitTestSuiteFolder());
			BECutAppContext.getContext().getUnitTestSuite().getBecutTestSuite().get().add(becutTestCase);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			closeWindow();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		runtimeEnviromentsBox.setItems(RuntimeEnvironment.getRuntimeOptions());
		runtimeEnviromentsBox.getSelectionModel().selectFirst();
		FXMLLoader loader = new FXMLLoader();
		try {
			compileListingNode = loader.load(
					getClass().getResource("/dk/bec/unittest/becut/ui/view/LoadCompileListing.fxml").openStream());
			compileListingController = loader.getController();
			compileListingPane.getChildren().add(compileListingNode);
			Preferences pref = Preferences.userNodeForPackage(this.getClass());
			jobName.setText(pref.get("jobNameRemembered", ""));
			programName.setText(pref.get("programNameRemembered", ""));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Stage getStage() {
		return (Stage) programName.getScene().getWindow();
	}
}
