package dk.bec.unittest.becut.recorder;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

import org.apache.commons.net.ftp.FTPClient;

import dk.bec.unittest.becut.Settings;
import dk.bec.unittest.becut.compilelist.CobolNodeType;
import dk.bec.unittest.becut.compilelist.TreeUtil;
import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.debugscript.ScriptGenerator;
import dk.bec.unittest.becut.ftp.FTPManager;
import dk.bec.unittest.becut.ftp.model.Credential;
import dk.bec.unittest.becut.ftp.model.DatasetProperties;
import dk.bec.unittest.becut.ftp.model.HostJob;
import dk.bec.unittest.becut.ftp.model.RecordFormat;
import dk.bec.unittest.becut.ftp.model.SequentialDatasetProperties;
import dk.bec.unittest.becut.ftp.model.SpaceUnits;
import dk.bec.unittest.becut.recorder.model.SessionRecording;
import dk.bec.unittest.becut.testcase.BecutTestCaseManager;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import koopa.core.trees.Tree;

public class RecorderManager {

	private static Random random = new Random();
	public static final String ITERATION_COUNTER_PREFIX = "BECUT-IC-";
	
	private RecorderManager() { }

	public static BecutTestCase recordBatch(CompileListing compileListing, String programName, String jobName, Credential credential) {
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
		
		
		try {
			// 1. Allocate dataset to save result
			FTPClient ftpClient = new FTPClient();
			String datasetName = credential.getUsername() + ".BECUT.T" + get6DigitNumber();
			DatasetProperties datasetProperties = new SequentialDatasetProperties(RecordFormat.FIXED_BLOCK, 80, 0, "", "", SpaceUnits.CYLINDERS, 2, 2);
			allocateDataset(ftpClient, credential, datasetName, datasetProperties);
			
			// 2. Generate JCL
			RecorderJCLGenerator jclGenerator = new RecorderJCLGenerator(programName, createDebugScript(compileListing, datasetName), jobName, credential.getUsername(), Settings.STEPLIB);
			
			// 3. Submit JCL and wait for it to complete
			InputStream jcl = new ByteArrayInputStream(jclGenerator.getJCL().getBytes()); 
			HostJob jobResult = FTPManager.submitJobAndWaitToComplete(ftpClient, jcl, 60, false);
			
			// 4. Download result dataset (from step 1) and delete
			String recordingResult = FTPManager.retrieveMember(ftpClient, datasetName);
			FTPManager.deleteMember(ftpClient, datasetName);
			
			// 5. Parse result file
			SessionRecording sessionRecording = DebugToolLogParser.Parse(recordingResult, compileListing.getProgramName());
			
			// 6. Create new testcase
			testCase = BecutTestCaseManager.createTestCaseFromSessionRecording(compileListing, sessionRecording);
			BufferedWriter bw = new BufferedWriter(new FileWriter("c:\\temp\\recording.txt"));
			bw.write(recordingResult);
			bw.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
		int number = random.nextInt(999999);
		return String.format("%06d", number);
	}
	
	public static String createDebugScript(CompileListing compileListing, String datasetName) {
		//TODO implement this in the script generator class
		
		String debugScript = "";
		debugScript += "            SET LOG ON FILE " + datasetName + ";\n";
		List<Tree> callStatements = TreeUtil.getDescendents(compileListing.getSourceMapAndCrossReference().getAst(), CobolNodeType.CALL_STATEMENT);
		for (Tree callStatement: callStatements) {
			int lineNumber = callStatement.getStartPosition().getLinenumber();
			String callProgramName = TreeUtil.stripQuotes(TreeUtil.getDescendents(callStatement, CobolNodeType.PROGRAM_NAME).get(0).getProgramText());
			String counterName = ITERATION_COUNTER_PREFIX + lineNumber + "-" + callProgramName;
			debugScript += "        01 " + counterName + " PIC 9(9) COMP;\n"; 
			//debugScript += "            MOVE 0 TO " + counterName + ";\n"; 
			debugScript += createCallStatementEntry(compileListing, callStatement, lineNumber, callProgramName, counterName);
		}
		
		return debugScript;
	}
	
	private static String createCallStatementEntry(CompileListing compileListing, Tree callStatement, int lineNumber, String callProgramName, String counterName) {
		String callEntry = "";
		
		int nextLineNumber = ScriptGenerator.findNextStatement(compileListing, callStatement);
		
		callEntry += "            STEP;\n";
		callEntry += "            AT " + lineNumber + "\n";
		callEntry += "            PERFORM\n";
		callEntry += "        COMPUTE " + counterName + " = \n";
		callEntry += "        " + counterName + " + 1;\n";
		callEntry += "            LIST  UNTITLED(\"START CALL " + lineNumber + ":" + callProgramName + "\");\n";
		callEntry += "            LIST  TITLED(" + counterName + ");\n";
		callEntry += "            LIST TITLED *;\n";
		callEntry += "            LIST  UNTITLED(\"END CALL " + lineNumber + ":" + callProgramName + "\");\n";
		callEntry += "            GO;\n";
		callEntry += "            END-PERFORM;\n";

		callEntry += "            STEP;\n";
		callEntry += "            AT " + nextLineNumber + "\n";
		callEntry += "            PERFORM\n";
		callEntry += "            LIST  UNTITLED(\"START AFTER CALL " + lineNumber + ":" + callProgramName + "\");\n";
		callEntry += "            LIST TITLED *;\n";
		callEntry += "            LIST  UNTITLED(\"END AFTER CALL " + lineNumber + ":" + callProgramName + "\");\n";
		callEntry += "            GO;\n";
		callEntry += "            END-PERFORM;\n";

		return callEntry;
	}
}
