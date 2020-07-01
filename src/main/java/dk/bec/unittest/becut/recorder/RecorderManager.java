package dk.bec.unittest.becut.recorder;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import org.apache.commons.net.ftp.FTPClient;

import dk.bec.unittest.becut.Settings;
import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.ftp.FTPManager;
import dk.bec.unittest.becut.ftp.model.Credential;
import dk.bec.unittest.becut.ftp.model.DatasetProperties;
import dk.bec.unittest.becut.ftp.model.RecordFormat;
import dk.bec.unittest.becut.ftp.model.SequentialDatasetProperties;
import dk.bec.unittest.becut.ftp.model.SpaceUnits;
import dk.bec.unittest.becut.recorder.model.SessionRecording;
import dk.bec.unittest.becut.testcase.BecutTestCaseManager;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;

public class RecorderManager {

	private static Random random = new Random();
	
	private RecorderManager() { }

	public static BecutTestCase recordBatch(CompileListing compileListing, String programName, String jobName, Credential credential) throws Exception {
		/*
		 * 1. Allocate dataset to save result
		 * 2. Generate JCL
		 * 3. Submit JCL and wait for it to complete
		 * 4. Download result dataset (from step 1) and delete
		 * 5. Parse result file
		 * 6. Create new testcase
		 * 7. return testcase
		 */
	
		BecutTestCase testCase = new BecutTestCase();
		
		// 1. Allocate dataset to save result
		FTPClient ftpClient = new FTPClient();
		String datasetName = credential.getUsername() + ".BECUT.T" + get6DigitNumber();
		DatasetProperties datasetProperties = new SequentialDatasetProperties(RecordFormat.FIXED_BLOCK, 80, 0, "", "", SpaceUnits.CYLINDERS, 2, 2);
		allocateDataset(ftpClient, credential, datasetName, datasetProperties);
		
		// 2. Generate JCL
		String jcl = RecorderJCLGenerator.getJCL(programName, datasetName, jobName, credential.getUsername(), Settings.STEPLIB);
		
		// 3. Submit JCL and wait for it to complete
		FTPManager.submitJobAndWaitToComplete(ftpClient, new ByteArrayInputStream(jcl.getBytes()), 60, false);
		
		// 4. Download result dataset (from step 1) and delete
		String recordingResult = FTPManager.retrieveMember(ftpClient, datasetName);
		FTPManager.deleteMember(ftpClient, datasetName);
		//TODO use OS independent path 
		Files.copy(new ByteArrayInputStream(recordingResult.getBytes()), Paths.get("/temp", datasetName));

		// 5. Parse result file
		SessionRecording sessionRecording = DebugToolLogParser.parseRecording(recordingResult, compileListing.getProgramName());
		
		// 6. Create new testcase
		testCase = BecutTestCaseManager.createTestCaseFromSessionRecording(compileListing, sessionRecording);

		// 8. return testcase
		return testCase;
	}

	public static void allocateDataset(FTPClient ftpClient, Credential credential, String datasetName, DatasetProperties datasetProperties) throws Exception {
		if (!ftpClient.isConnected()) {
			FTPManager.connectAndLogin(ftpClient, credential);
		}
		FTPManager.allocateDataset(ftpClient, datasetName, datasetProperties);
	}

	public static String get6DigitNumber() {
		return String.format("%06d", random.nextInt(999999));
	}
}
