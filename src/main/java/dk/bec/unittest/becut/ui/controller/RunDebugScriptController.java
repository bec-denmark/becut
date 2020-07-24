package dk.bec.unittest.becut.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import dk.bec.unittest.becut.DDNAME;
import dk.bec.unittest.becut.Settings;
import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.debugscript.DebugScriptExecutor;
import dk.bec.unittest.becut.debugscript.ScriptGenerator;
import dk.bec.unittest.becut.debugscript.model.DebugScript;
import dk.bec.unittest.becut.ftp.model.HostJob;
import dk.bec.unittest.becut.ftp.model.HostJobDataset;
import dk.bec.unittest.becut.recorder.DebugToolLogParser;
import dk.bec.unittest.becut.recorder.model.SessionRecording;
import dk.bec.unittest.becut.testcase.PostConditionResolver;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import dk.bec.unittest.becut.testcase.model.PostConditionResult;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import dk.bec.unittest.becut.ui.model.RuntimeEnvironment;
import dk.bec.unittest.becut.ui.view.StandardAlerts;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RunDebugScriptController extends AbstractBECutController implements Initializable {
	private static String loadModuleNameRemembered;
	
	@FXML
	private TextField loadModuleName;
	
	@FXML
	private ComboBox<String> runtimeEnviromentsBox;
	
	@FXML 
	private TextField jobName;
	
	@FXML
	private void cancel() {
		loadModuleName.getScene().getWindow().hide();
	}
	
	@FXML 
	protected void ok() {
		try {
			BecutTestCase becutTestCase = BECutAppContext.getContext().getUnitTest().getBecutTestCase();
			DebugScript debugScript = ScriptGenerator.generateDebugScript(becutTestCase);
			String programName = becutTestCase.getProgramName();
			if (!loadModuleName.getText().isEmpty()) {
				programName = loadModuleName.getText();
				loadModuleNameRemembered = loadModuleName.getText();
			}
	
			HostJob job = DebugScriptExecutor.testBatch(jobName.getText(), programName, debugScript);
			HostJobDataset jobDataset = job.getDataset(DDNAME.INSPLOG);
			//TODO show jesysmsg if job status != cc0000
			if(jobDataset == null) {
				throw new MissingINSPLOGException(job.getDataset(DDNAME.SYSOUT).getContents());
			}
			SessionRecording sessionRecording = DebugToolLogParser.parseRunning(jobDataset.getContents(), programName);
			PostConditionResult postConditionResult = PostConditionResolver.verify(becutTestCase, sessionRecording);
			StandardAlerts.informationDialog("Test Case result", "Result running test case " 
					+ becutTestCase.getTestCaseId(), postConditionResult.prettyPrint());
			//TODO quick and dirty messaging
			throw new LogMessage(job.getDataset(DDNAME.SYSOUT).getContents());
			//TODO Present to result to the user in a meaningful way
		} finally {
			closeWindow();
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		runtimeEnviromentsBox.setItems(RuntimeEnvironment.getRuntimeOptions());
		runtimeEnviromentsBox.getSelectionModel().selectFirst();
		jobName.setText(Settings.BATCH_JOBNAME_EXECUTE_TEST);
		if(loadModuleNameRemembered != null) {
			loadModuleName.setText(loadModuleNameRemembered);
		}
	}

	@Override
	protected Stage getStage() {
		return (Stage) loadModuleName.getScene().getWindow();
	}

}
