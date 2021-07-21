package dk.bec.unittest.becut.recorder;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;

import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.debugscript.DebugScriptExecutor;
import dk.bec.unittest.becut.debugscript.JCLTemplate;
import dk.bec.unittest.becut.ftp.FTPManager;
import dk.bec.unittest.becut.ftp.model.Credential;
import dk.bec.unittest.becut.ftp.model.DatasetProperties;
import dk.bec.unittest.becut.ftp.model.RecordFormat;
import dk.bec.unittest.becut.ftp.model.SequentialDatasetProperties;
import dk.bec.unittest.becut.ftp.model.SpaceUnits;
import dk.bec.unittest.becut.recorder.model.SessionRecording;
import dk.bec.unittest.becut.testcase.BecutTestCaseSuiteManager;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import dk.bec.unittest.becut.ui.model.BECutAppContext;

public class RecorderManager {

	private RecorderManager() { }

	public static BecutTestCase recordBatch(
			BECutAppContext ctx,
			String jobName,
			Path datasetsPath) throws Exception {
		/*
		 * 1. Allocate dataset to save result
		 * 2. Generate JCL
		 * 3. Upload DDs needed by a program
		 * 4. Submit JCL and wait for it to complete
		 * 5. Download result dataset (from step 1) and delete
		 * 6. Parse result file
		 * 7. Create new testcase
		 * 8. return testcase
		 */
	
		Credential credential = ctx.getCredential();
		CompileListing compileListing = ctx.getUnitTestSuite().getCompileListing();
		String programName = compileListing.getProgramName();
		// 1. Allocate dataset to save result
		FTPClient ftpClient = new FTPClient();
		String insplog = DebugScriptExecutor.randomDDName(credential.getUsername(), programName, "INSPLOG"); 
		
		DatasetProperties datasetProperties = new SequentialDatasetProperties(
				RecordFormat.VARIABLE, 256, 0, "", "", SpaceUnits.CYLINDERS, 2, 2);
		allocateDataset(ftpClient, credential, insplog, datasetProperties);

		String user = credential.getUsername();
		Map<String, String> datasetNames = DebugScriptExecutor.generateDDnames(compileListing, user, programName);
		DebugScriptExecutor.putDatasets(compileListing, ftpClient, datasetsPath, datasetNames, user);
		
		// 2. Generate JCL
		Path scriptPath = ctx.getRecordScriptPath();
		if (!Files.exists(scriptPath)) {
    		List<String> jcl = JCLTemplate.recording(compileListing);
    		Files.write(scriptPath, jcl);
		}
		List<String> jclTemplate = Files.readAllLines(scriptPath);
		
		String jcl = JCLTemplate.fillTemplate(jclTemplate, 
				user, programName, jobName, DebugScriptExecutor.jclDDs(datasetNames), "", insplog);
		
		// 3. Submit JCL and wait for it to complete
		FTPManager.submitJobAndWaitToComplete(ftpClient, new ByteArrayInputStream(jcl.getBytes()), 60, false);
		
		// 4. Download result dataset (from step 1) and delete
		String recordingResult = FTPManager.retrieveMember(ftpClient, insplog);
		//FTPManager.deleteMember(ftpClient, datasetName);

		// 5. Parse result file
		SessionRecording sessionRecording = DebugToolLogParser.parseRecording(recordingResult);
		
		// 6. Create new testcase
		BecutTestCase testCase = BecutTestCaseSuiteManager.createTestCaseFromSessionRecording(compileListing, sessionRecording);

		// 8. return testcase
		return testCase;
	}

	public static void allocateDataset(FTPClient ftpClient, Credential credential, String datasetName, DatasetProperties datasetProperties) throws Exception {
		if (!ftpClient.isConnected()) {
			FTPManager.connectAndLogin(ftpClient, credential);
		}
		FTPManager.allocateDataset(ftpClient, datasetName, datasetProperties);
	}
}