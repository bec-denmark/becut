package dk.bec.unittest.becut.debugscript;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.apache.commons.net.ftp.FTPClient;

import dk.bec.unittest.becut.Settings;
import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.debugscript.model.DebugScript;
import dk.bec.unittest.becut.ftp.FTPManager;
import dk.bec.unittest.becut.ftp.model.Credential;
import dk.bec.unittest.becut.ftp.model.DatasetProperties;
import dk.bec.unittest.becut.ftp.model.HostJob;
import dk.bec.unittest.becut.ftp.model.RecordFormat;
import dk.bec.unittest.becut.ftp.model.SequentialDatasetProperties;
import dk.bec.unittest.becut.ftp.model.SpaceUnits;
import dk.bec.unittest.becut.recorder.RecorderManager;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import dk.bec.unittest.becut.testcase.model.Parameter;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import dk.bec.unittest.becut.ui.model.UnitTest;
import koopa.core.trees.Tree;
import koopa.core.trees.jaxen.Jaxen;

public class DebugScriptExecutor {
	static final String $USER = "${user}";
	static final String $PROGRAM = "${program}";
	static final String $JOBNAME = "${jobname}";
	static final String $DD = "${dd}";
	
	public static HostJob testBatch(String jobName, String programName, DebugScript debugScript) {
		FTPClient ftpClient = new FTPClient();
		try {
			Credential credential = BECutAppContext.getContext().getCredential();
			FTPManager.connectAndLogin(ftpClient, credential);
			
			BecutTestCase becutTestCase = BECutAppContext.getContext().getUnitTest().getBecutTestCase();
			
			if(becutTestCase.getDebugScriptPath() == null || !Files.exists(becutTestCase.getDebugScriptPath())) {
				Path debugScriptPath = Paths.get(becutTestCase.getBecutTestCaseDir().toString(), "/debug_script.txt");
				Files.write(debugScriptPath, createJCL(becutTestCase));
				becutTestCase.setDebugScriptPath(debugScriptPath);
			}
			
			String user = credential.getUsername();
			Map<String, String> datasetNames = generateDDnames(becutTestCase.getCompileListing(), user);
			putDatasets(ftpClient, becutTestCase, datasetNames, user);
			List<String> jclTemplate = Files.readAllLines(becutTestCase.getDebugScriptPath());
			String DDs = jclDDs(datasetNames);
			String jcl = fillTemplate(jclTemplate, user, programName, "BECUT", DDs);
			
			InputStream is = new ByteArrayInputStream(jcl.getBytes());
			//TODO delete test datasets
			return FTPManager.submitJobAndWaitToComplete(ftpClient, is, 60, true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static String fillTemplate(List<String> lines, String user, String program, String jobName, String dd) {
		return lines
				.stream()
				.map(line -> {
					//TODO remove repetition
					//TODO watch out for cAsEs
					if(line.contains($USER)) {
						line = line.replace($USER, user);
					}
					if(line.contains($PROGRAM)) {
						line = line.replace($PROGRAM, program);
					}
					if(line.contains($JOBNAME)) {
						line = line.replace($JOBNAME, jobName);
					}
					if(line.contains($DD)) {
						line = line.replace($DD, dd);
					}
					return line;
				})
				.collect(Collectors.joining("\n"));
	}
	
	public static List<String> createJCL(BecutTestCase becutTestCase) {
		String debugScript = ScriptGenerator.generateDebugScript(becutTestCase).generate();
		return createJCL(debugScript);
	}
	
	public static List<String> createJCL(String debugScript) {
		return new ArrayList<String>(Arrays.asList(
				"//${jobname} JOB ,'" + $USER + "',",
				"//             SCHENV=TSTSYS,",
				"//             MSGCLASS=Q,",
				"//             NOTIFY=" + $USER + ",",
				"//             UJOBCORR=" + $USER + "",
				"//PGMEXEC  EXEC PGM=" + $PROGRAM,
				generateSteplib(Settings.STEPLIB),
				//placeholder for program DDs
				//will be filled before submit
				$DD,
				//generateDDs(ftpClient, userName);
				"//INSPIN      DD *",
				debugScript,
				"",
				"/*",
				"//INSPLOG   DD SYSOUT=*",
				//"//INSPCMD   DD DSN=SYS2.DEBUG.COMMANDS,DISP=SHR\n";
				"//CEEOPTS   DD *,DLM='/*'",
				"TEST(,INSPIN,,)",
				"/*"
		));
	}
	
	private static String generateSteplib(List<String> steplibs) {
		return 
			steplibs
				.stream()
				.map(s -> "//STEPLIB   DD DSN=" + s.toUpperCase() + ",DISP=SHR")
				.collect(Collectors.joining("\n", "", ""));
	}
	
	private static Map<String, String> generateDDnames(CompileListing compileListing, String userName) {
		return compileListing.getSourceMapAndCrossReference().getFileControlAssignment().entrySet()
				.stream()
				.map(entry -> {
						//TODO put all files in one PDS username.becut.random(assign to name)
						String datasetName =  
								userName.toUpperCase() + 
								".BECUT.T" + 
								RecorderManager.get6DigitNumber();
						return new String[] {entry.getValue(), datasetName};
				})
				.collect(Collectors.toMap(e -> e[0], e -> e[1]));
		
	}
	
	private static String jclDDs(Map<String, String> datasetNames) {
		return datasetNames.entrySet()
			.stream()
			.map(e -> "//" + e.getKey() + "    DD DSN=" + e.getValue() + ",DISP=SHR")
			.collect(Collectors.joining("\n", "", ""));
	}
	
	private static void putDatasets(FTPClient ftpClient, BecutTestCase becutTestCase, 
			Map<String, String> datasetNames, String userName) {
		CompileListing compileListing = becutTestCase.getCompileListing();
		compileListing.getSourceMapAndCrossReference().getFileControlAssignment()
			.entrySet()
			.stream()
			.forEach(entry -> {
				String fileName = entry.getKey();
				String recordName = compileListing.getSourceMapAndCrossReference().getFileSection().get(fileName);
				
				List<Parameter> params = becutTestCase.getPreCondition().getFileSection();
				Optional<Parameter> op = params
						.stream()
						.filter(p -> p.getName().equals(recordName))
						.findFirst();
				int size = op.isPresent() ? op.get().getSize() : 80;

				//TODO put all files in one PDS username.becut.random(assign to name)
				String datasetName = datasetNames.get(entry.getValue()); 
				DatasetProperties datasetProperties = 
						new SequentialDatasetProperties(
								RecordFormat.FIXED_BLOCK, size, 0, "", "", SpaceUnits.CYLINDERS, 2, 2);
				Map<String, File> map = becutTestCase.getAssignmentLocalFile();
				Path localPath = map.get(entry.getValue()).toPath();
				if(Files.exists(localPath)) {
					checkRecordsLength(localPath, size);
					FTPManager.sendDataset(ftpClient, datasetName, localPath.toFile(), datasetProperties);
				} else {
					FTPManager.allocateDataset(ftpClient, datasetName, datasetProperties);
				};				
			});
	}
	
	private static void checkRecordsLength(Path file, int size) {
		Map<Integer, Integer> lines = new LinkedHashMap<>();
		AtomicInteger ai = new AtomicInteger();
		try {
			Files.readAllLines(file)
				.forEach(line -> {
					if (line.length() < size) {
						lines.put(ai.get(), line.length());
					}
					ai.getAndIncrement();
				});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if(lines.size() > 0) {
			throw new RuntimeException(
					String.format("records in %s file are too short, possible ABEND S0C7\n%s", file, lines));
		}
	}
}
