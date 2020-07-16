package dk.bec.unittest.becut.debugscript;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
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
import dk.bec.unittest.becut.testcase.model.Parameter;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import koopa.core.trees.Tree;
import koopa.core.trees.jaxen.Jaxen;

public class DebugScriptExecutor {
	
	public static HostJob testBatch(String jobName, String programName, DebugScript debugScript) {
		FTPClient ftpClient = new FTPClient();
		try {
			FTPManager.connectAndLogin(ftpClient, BECutAppContext.getContext().getCredential());
			InputStream is = new ByteArrayInputStream(createJCL(jobName, programName, debugScript.generate()).getBytes());
			return FTPManager.submitJobAndWaitToComplete(ftpClient, is, 60, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static String createJCL(String jobName, String programName, String debugScript) {
		String userName = Settings.USERNAME;
		String jcl = "//" +  jobName + " JOB ,'" + userName + "',\n";
		jcl += "//             SCHENV=TSTSYS,\n";
		jcl += "//             MSGCLASS=Q,\n";
		jcl += "//             NOTIFY=" + userName +",\n";
		jcl += "//             UJOBCORR=" + userName +"\n";
		jcl += "//PGMEXEC  EXEC PGM=" + programName + "\n";
		jcl += generateSteplib();
		jcl += generateDDs();
		jcl += "//INSPIN      DD *\n";
		jcl += debugScript;
		jcl += "\n";
		jcl += "/*\n";
		jcl += "//INSPLOG   DD SYSOUT=*\n";
		jcl += "//INSPCMD   DD DSN=SYS2.DEBUG.COMMANDS,DISP=SHR\n";
		jcl += "//CEEOPTS   DD *,DLM='/*'\n";
		jcl += "TEST(,INSPIN,,)\n";
		jcl += "/*\n";
		
		return jcl;
	}
	
	private static String generateSteplib() {
		List<String> steplib = Settings.STEPLIB;
		String s = "//STEPLIB   DD DSN=" + steplib.get(0).toUpperCase() + ",DISP=SHR\n";
		if (steplib.size() > 1) {
			for (int i = 1; i < steplib.size(); i++) {
			s += "//          DD DSN=" + steplib.get(i).toUpperCase() + ",DISP=SHR\n";
			}
		}
		return s;
	}
	
	private static String generateDDs() {
		CompileListing compileListing = BECutAppContext.getContext().getUnitTest().getCompileListing();
		return compileListing.getSourceMapAndCrossReference().getFileControlAssignments().entrySet()
			.stream()
			.map(entry -> {
					FTPClient ftpClient = new FTPClient();
					Credential credential = BECutAppContext.getContext().getCredential();
					if (!ftpClient.isConnected()) {
						try {
							FTPManager.connectAndLogin(ftpClient, credential);
						} catch (Exception e) {
							//FIXME
							throw new RuntimeException(e);
						}
					}
					String fileName = entry.getKey();
					
					String recordName = Jaxen.evaluate(
							compileListing.getSourceMapAndCrossReference().getAst(),
							"//fileDescriptionEntry[//fileName//node()/text()=\"" +
							fileName + 
							"\"]//following-sibling::recordDescriptionEntry[1]//dataName//text()")
							.stream()
							.map(Tree.class::cast)
							.map(Tree::getText)
							.collect(Collectors.joining());
					
					//TODO fix this telescope
					List<Parameter> params = BECutAppContext.getContext().getUnitTest()
							.getBecutTestCase().getPreCondition().getFileSection();
					Optional<Parameter> op = params
							.stream()
							.filter(p -> p.getName().equals(recordName))
							.findFirst();
					int size = op.isPresent() ? op.get().getSize() : 80;
					//TODO put all files in one PDS username.becut.random(assign to name)
					String datasetName = credential.getUsername() + 
							".BECUT.T" + 
							RecorderManager.get6DigitNumber() +
							"." + entry.getValue();
					DatasetProperties datasetProperties = 
							new SequentialDatasetProperties(
									RecordFormat.FIXED_BLOCK, size, 0, "", "", SpaceUnits.CYLINDERS, 2, 2);
					try {
						Path localPath = Paths.get("/temp/", entry.getValue() + ".txt");
						if(Files.exists(localPath)) {
							FTPManager.sendDataset(ftpClient, datasetName, 
									new File("/temp/" + entry.getValue() + ".txt"), datasetProperties);
						} else {
							FTPManager.allocateDataset(ftpClient, datasetName, datasetProperties);
						}
					} catch (Exception e) {
						//FIXME
						throw new RuntimeException(e);
					}
					return "//" + entry.getValue() + "    DD DSN=" + datasetName + ",DISP=SHR";})
			.collect(Collectors.joining("\n", "", "\n"));
	}
}
