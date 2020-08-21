package dk.bec.unittest.becut.recorder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.net.ftp.FTPClient;

import dk.bec.unittest.becut.Settings;
import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.ftp.FTPManager;
import dk.bec.unittest.becut.ftp.model.DatasetProperties;
import dk.bec.unittest.becut.ftp.model.RecordFormat;
import dk.bec.unittest.becut.ftp.model.SequentialDatasetProperties;
import dk.bec.unittest.becut.ftp.model.SpaceUnits;
import dk.bec.unittest.becut.testcase.model.Parameter;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import koopa.core.trees.Tree;
import koopa.core.trees.jaxen.Jaxen;

public class RecorderJCLGenerator {
	public static String getJCL(FTPClient ftpClient, String programName, String datasetName, String jobName, String userName) {
		String depthOfCall = String.format("BECUT-%03d-DEPTH", new Random().nextInt(1000));
		String jcl = "" +
				"//" +  jobName + " JOB ,'" + userName + "',\n" +
				"//             SCHENV=TSTSYS,\n" +
				"//             MSGCLASS=Q,\n" +
				"//             NOTIFY=" + userName +",\n" + 
				"//             UJOBCORR=" + userName +"\n" +
				"//PGMEXEC  EXEC PGM=" + programName + "\n" +
				generateSteplib(Settings.STEPLIB) + 
				generateDDs(ftpClient, userName) + 
				"//INSPIN    DD *\n" + 
				"            SET LOG ON FILE " + datasetName + ";\n" + 
				"            SET DYNDEBUG OFF;                            \n" + 
				"            77 " + depthOfCall + " PIC 9(9) COMP;\n" +
				"            MOVE 0 TO " + depthOfCall + ";\n" + 
				"            AT CALL * BEGIN;                             \n" +
				"               IF " + depthOfCall + " < 1 THEN                 \n" + 
				"                 LIST UNTITLED('AT CALL BEGIN');         \n" + 
				"                 LIST CALLS;                             \n" + 
				"                 LIST TITLED *;                          \n" + 
				"                 LIST UNTITLED('AT CALL END');           \n" + 
				"                 LIST UNTITLED('');                      \n" + 
				"               END-IF;                                   \n" + 
				"        COMPUTE " + depthOfCall + " =  " + depthOfCall + " + 1;\n" + 
				"               GO;                                       \n" + 
				"            END;                                         \n" + 
				"            AT EXIT * BEGIN;                             \n" + 
				"               IF " + depthOfCall + " < 2 THEN                 \n" + 
				"                 LIST UNTITLED('AT EXIT BEGIN');         \n" + 
				"                 LIST %PROGRAM;                          \n" + 
				"                 LIST CALLS;                             \n" + 
				"                 LIST TITLED *;                          \n" + 
				"                 LIST UNTITLED('AT EXIT END');           \n" + 
				"                 LIST UNTITLED('');                      \n" + 
				"               END-IF;                                   \n" + 
				"        COMPUTE " + depthOfCall + " =  " + depthOfCall + " - 1;\n" + 
				"               GO;                                       \n" + 
				"            END;                                         \n" + 
				"            GO;                                          \n" + 
				"            QUIT;                                        " + 
				"/*\n" + 
				"//INSPLOG   DD SYSOUT=*\n" + 
//				"//INSPCMD   DD DSN=SYS2.DEBUG.COMMANDS,DISP=SHR\n" + 
				"//CEEOPTS   DD *,DLM='/*'\n" + 
				"TEST(,INSPIN,,)\n" + 
				"/*";
		
		return jcl.toUpperCase();
	}
	
	private static String generateSteplib(List<String> steplibs) {
		String first = "//STEPLIB   DD DSN=" + steplibs.get(0).toUpperCase() + ",DISP=SHR\n";
		return first +
			steplibs.subList(1, steplibs.size())
				.stream()
				.map(s -> "//          DD DSN=" + s.toUpperCase() + ",DISP=SHR")
				.collect(Collectors.joining("\n", "", ""));
	}

	//TODO code deduplication in DebugsSriptExecutor and here
	private static String generateDDs(FTPClient ftpClient, String userName) {
		CompileListing compileListing = BECutAppContext.getContext().getUnitTestSuite().getCompileListing();
		return compileListing.getSourceMapAndCrossReference().getFileControlAssignment().entrySet()
			.stream()
			.map(entry -> {
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
					List<Parameter> params = BECutAppContext.getContext().getUnitTestSuite().getBecutTestCaseSuite().get()
							.get(0).getPreCondition().getFileSection();
					Optional<Parameter> op = params
							.stream()
							.filter(p -> p.getName().equals(recordName))
							.findFirst();
					int size = op.isPresent() ? op.get().getSize() : 80;

					//TODO put all files in one PDS username.becut.random(assign to name)
					String datasetName =  
							userName.toUpperCase() + 
							".BECUT.T" + 
							RecorderManager.get6DigitNumber() +
							"." + entry.getValue();
					DatasetProperties datasetProperties = 
							new SequentialDatasetProperties(
									RecordFormat.FIXED_BLOCK, size, 0, "", "", SpaceUnits.CYLINDERS, 2, 2);
					Path localPath = Paths.get("/temp/", entry.getValue() + ".txt");
					if(Files.exists(localPath)) {
						FTPManager.sendDataset(ftpClient, datasetName, new File("/temp/" + entry.getValue() + ".txt"), datasetProperties);
					} else {
						FTPManager.allocateDataset(ftpClient, datasetName, datasetProperties);
					}
					return "//" + entry.getValue() + "    DD DSN=" + datasetName + ",DISP=SHR";})
			.collect(Collectors.joining("\n", "", "\n"));
	}
}
