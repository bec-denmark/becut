package dk.bec.unittest.becut.ui.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import dk.bec.unittest.becut.Settings;
import dk.bec.unittest.becut.debugscript.DebugScriptExecutor;
import dk.bec.unittest.becut.ftp.model.JobResult;
import dk.bec.unittest.becut.recorder.DebugToolLogParser;
import dk.bec.unittest.becut.recorder.model.SessionRecording;
import dk.bec.unittest.becut.testcase.PostConditionResolver;
import dk.bec.unittest.becut.testcase.model.BecutTestCaseSuite;
import dk.bec.unittest.becut.testcase.model.PostConditionResult;
import dk.bec.unittest.becut.testcase.model.TestResult;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import dk.bec.unittest.becut.ui.model.RuntimeEnvironment;
import dk.bec.unittest.becut.ui.view.StandardAlerts;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RunSuiteController extends AbstractBECutController implements Initializable {
	private static String loadModuleNameRemembered;
	
	@FXML
	private TextField loadModuleName;
	
	@FXML
	private ComboBox<String> runtimeEnviromentsBox;
	
	@FXML 
	private TextField jobName;
	
	@FXML
	private void cancel() {
		closeWindow();
	}
	
	@FXML 
	protected void ok() {
		try {
			BECutAppContext.getContext().getEventBus().post(new LogController.ClearEvent());
			BecutTestCaseSuite testSuite = BECutAppContext.getContext().getUnitTestSuite().getBecutTestCaseSuite().get();
			List<String> results = new ArrayList<>();
			testSuite.forEach(becutTestCase -> {
				String programName = becutTestCase.getProgramName();
				if (!loadModuleName.getText().isEmpty()) {
					programName = loadModuleName.getText();
					loadModuleNameRemembered = loadModuleName.getText();
				}
		
				JobResult jobResult = DebugScriptExecutor.testBatch(becutTestCase, jobName.getText(), programName);
				if(!"RC=0000".equals(jobResult.rc)) {
					results.add(becutTestCase.getTestCaseName() + "\t" + jobResult.rc);
					BECutAppContext.getContext().getEventBus().post(jobResult);
				} else {
					SessionRecording sessionRecording = DebugToolLogParser.parseRunning(jobResult.spool);
					PostConditionResult postConditionResult = PostConditionResolver.verify(becutTestCase, sessionRecording);
					results.add(becutTestCase.getTestCaseName() + "\t" + postConditionResult.prettyPrint());
					BECutAppContext.getContext().getEventBus().post(new TestResult(becutTestCase, postConditionResult));
				}
			});
			StandardAlerts.informationDialog("Test suite results", "Result running test suite", 
					results
						.stream()
						.collect(Collectors.joining(" ")));
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
