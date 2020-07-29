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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
	public static HostJob testBatch(String jobName, String programName) {
		FTPClient ftpClient = new FTPClient();
		try {
			Credential credential = BECutAppContext.getContext().getCredential();
			FTPManager.connectAndLogin(ftpClient, credential);
			
			BecutTestCase becutTestCase = BECutAppContext.getContext().getUnitTest().getBecutTestCase();
			
    		Path debugScriptPath = BECutAppContext.getContext().getDebugScriptPath();
    		if (!Files.exists(debugScriptPath)) {
        		List<String> jcl = DebugScriptTemplate.createJCLTemplate();
        		Files.write(debugScriptPath, jcl);
    		}
			
			String user = credential.getUsername();
			Map<String, String> datasetNames = generateDDnames(becutTestCase.getCompileListing(), user);
			putDatasets(ftpClient, becutTestCase, datasetNames, user);
			List<String> jclTemplate = Files.readAllLines(debugScriptPath);
			String DDs = jclDDs(datasetNames);
			String debugScript = ScriptGenerator.generateDebugScript(becutTestCase).generate();
			String jcl = DebugScriptTemplate.fillTemplate(jclTemplate, user, programName, "BECUT", DDs, debugScript);
			
			InputStream is = new ByteArrayInputStream(jcl.getBytes());
			//TODO delete test datasets
			return FTPManager.submitJobAndWaitToComplete(ftpClient, is, 60, true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
				Path localPath = Paths.get(BECutAppContext.getContext().getUnitTestFolder().toString(), entry.getValue() + ".txt");
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
			System.out.println(String.format("records in %s file are too short, possible ABEND S0C7\n%s", file, lines));
//			throw new RuntimeException(
//					String.format("records in %s file are too short, possible ABEND S0C7\n%s", file, lines));
		}
	}
}
