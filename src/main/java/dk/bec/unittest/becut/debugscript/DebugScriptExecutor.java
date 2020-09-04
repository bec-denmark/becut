package dk.bec.unittest.becut.debugscript;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileEntryParser;
import org.apache.commons.net.ftp.parser.FTPFileEntryParserFactory;
import org.apache.commons.net.ftp.parser.ParserInitializationException;

import dk.bec.unittest.becut.compilelist.CobolNodeType;
import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.ftp.FTPManager;
import dk.bec.unittest.becut.ftp.model.Credential;
import dk.bec.unittest.becut.ftp.model.DatasetProperties;
import dk.bec.unittest.becut.ftp.model.JobResult;
import dk.bec.unittest.becut.ftp.model.RecordFormat;
import dk.bec.unittest.becut.ftp.model.SequentialDatasetProperties;
import dk.bec.unittest.becut.ftp.model.SpaceUnits;
import dk.bec.unittest.becut.testcase.BecutTestCaseSuiteManager;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import dk.bec.unittest.becut.testcase.model.Parameter;
import dk.bec.unittest.becut.ui.model.BECutAppContext;

public class DebugScriptExecutor {
	public static JobResult testBatch(BECutAppContext ctx, BecutTestCase becutTestCase, String jobName, String programName) {
		//TODO cleanup files in finally
		FTPClient ftpClient = new FTPClient();
		try {
			Credential credential = ctx.getCredential();
			FTPManager.connectAndLogin(ftpClient, credential);
			
    		Path scriptPath = ctx.getTestScriptPath();
    		if (!Files.exists(scriptPath)) {
        		List<String> jcl = JCLTemplate.generic();
        		Files.write(scriptPath, jcl);
    		}
    		List<String> jclTemplate = Files.readAllLines(scriptPath);
			
			String user = credential.getUsername();
			CompileListing compileListing = ctx.getUnitTestSuite().getCompileListing();
			
			Map<String, String> datasetNames = generateDDnames(compileListing, user, programName);
			Path base = Paths.get(ctx.getUnitTestSuiteFolder().toString(), becutTestCase.getTestCaseName());
			putDatasets(compileListing, ftpClient, base, datasetNames, user);
			
			String inspLog = randomDDName(user, programName);
			allocateInspLog(ftpClient, inspLog);

			String debugScript = ScriptGenerator.generateDebugScript(compileListing, becutTestCase, inspLog).generate();
			
			String DDs = jclDDs(datasetNames);
			String jcl = JCLTemplate.fillTemplate(jclTemplate, user, programName, jobName, DDs, debugScript, "");
			
			InputStream is = new ByteArrayInputStream(jcl.getBytes());
			String jobId = submitJob(ftpClient, is);
			return retriveJobResult(ftpClient, jobId, inspLog, TimeUnit.SECONDS, 60);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void allocateInspLog(FTPClient ftpClient, String inspLog) {
		DatasetProperties datasetProperties = 
				new SequentialDatasetProperties(
						RecordFormat.FIXED_BLOCK, 80, 0, "", "", SpaceUnits.CYLINDERS, 2, 2);
		FTPManager.allocateDataset(ftpClient, inspLog, datasetProperties);
	}
	
	static String submitJob(FTPClient ftp, InputStream jcl) throws Exception {
		ftp.site("JESOWNER=*");
		ftp.site("JESJOBNAME=*");
		ftp.site("FILETYPE=JES");
		
		ftp.storeFile("TNONAME0", jcl);
		return getJobId(ftp.getReplyString());
	}
	
	static JobResult retriveJobResult(FTPClient ftp, String jobId, String inspLog, TimeUnit units, int time) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(100000);
		ftp.setBufferSize(16384);
		
		ftp.site("JESOWNER=*");
		ftp.site("JESJOBNAME=*");
		ftp.site("FILETYPE=JES");
		
		AtomicBoolean jobIsDone = new AtomicBoolean(false);
		AtomicReference<String> rc = new AtomicReference<>(); 
		
		final class MyHostFTPFileEntryParserFactory implements FTPFileEntryParserFactory {
			final FTPFileEntryParser parser = new FTPFileEntryParser() {
				@Override
				public FTPFile parseFTPEntry(String line) {
					return null;
				}

				Pattern p1 = Pattern.compile("JOBNAME\\s+JOBID\\s+OWNER\\s+STATUS\\s+CLASS");
				//"OUTPUT ... The job has finished and has output to be printed or retrieved."
				Pattern p2 = Pattern.compile("\\S+\\s+\\S+\\s+\\S+\\s+OUTPUT\\s+\\S+\\s+(.+)");
				@Override
				public List<String> preParse(List<String> lines) {
					//JOBNAME  JOBID    OWNER    STATUS CLASS
					//BECUTJOB JOB55818 ZT4      OUTPUT A        RC=0000
					if(lines.size() > 1) {
						if(p1.matcher(lines.get(0)).matches()) {
							Matcher m = p2.matcher(lines.get(1));
							if(m.matches()) {
								jobIsDone.set(true);
								rc.set(m.group(1).trim());
							}
						}
					}
					//lines.forEach(System.out::println);
					return lines;
				}

				@Override
				public String readNextEntry(BufferedReader br) throws IOException {
					return br.readLine();
				}
			};
			
			@Override
			public FTPFileEntryParser createFileEntryParser(String key) throws ParserInitializationException {
				return parser;
			}

			@Override
			public FTPFileEntryParser createFileEntryParser(FTPClientConfig config) throws ParserInitializationException {
				return parser;
			}
		}
		
		ftp.setParserFactory(new MyHostFTPFileEntryParserFactory());
		
		while(!jobIsDone.get()) {
			ftp.listFiles(jobId);
			Thread.sleep(200);
		}
		
//		ftp.retrieveFile(jobId, outputStream);
//		if (!ftp.getReplyString().substring(0, 3).equals("250")) {
//			throw new Exception(ftp.getReplyString());
//		}

		ftp.site("FILETYP=SEQ");
		ftp.retrieveFile(inspLog, outputStream);
//		if (!ftp.getReplyString().substring(0, 3).equals("250")) {
//		throw new Exception(ftp.getReplyString());
//	}
		//there is no SUCCESS_MARKER in insplog or RC != CC 0000
		//something went wrong and spool must be checked
		
		return new JobResult(rc.get(), Arrays.asList(outputStream.toString("Cp1252").split("\\r?\\n")));
	}
	
	public static Map<String, String> generateDDnames(CompileListing compileListing, String... parts) {
		return generateDDnames(compileListing, new ArrayList<>(Arrays.asList(parts)));
	}
	
	private static Map<String, String> generateDDnames(CompileListing compileListing, List<String> parts) {
		return compileListing.getSourceMapAndCrossReference().getFileControlAssignment().entrySet()
				.stream()
				.map(entry -> {
						parts.add(entry.getValue());
						String datasetName =  randomDDName(parts);
						return new String[] {entry.getValue(), datasetName};
				})
				.collect(Collectors.toMap(e -> e[0], e -> e[1]));
		
	}

	public static String randomDDName(String... parts) {
		return randomDDName(new ArrayList<>(Arrays.asList(parts)));
	}
	
	private static String randomDDName(List<String> parts) {
		return parts
				.stream()
				.map(String::toUpperCase)
				.collect(Collectors.joining(".", "", ".BECUT.T" + get6DigitNumber()));
	}
	
	public static String jclDDs(Map<String, String> datasetNames) {
		return datasetNames.entrySet()
			.stream()
			.map(e -> "//" + e.getKey() + "   DD DSN=" + e.getValue() + ",DISP=SHR")
			.collect(Collectors.joining("\n", "", ""));
	}
	
	public static void putDatasets(CompileListing compileListing, FTPClient ftpClient, Path basePath, Map<String, String> datasetNames, String userName) {
		compileListing.getSourceMapAndCrossReference().getFileControlAssignment()
			.entrySet()
			.stream()
			.forEach(entry -> {
				String fileName = entry.getKey();
				String recordName = compileListing.getSourceMapAndCrossReference().getFileSection().get(fileName);
				
				List<Parameter> params = BecutTestCaseSuiteManager.parseRecordsFromSection(compileListing, CobolNodeType.FILE_SECTION);
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
				Path localPath = Paths.get(basePath.toString(), entry.getValue() + ".txt");
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
			System.err.println(String.format("records in %s file are too short, possible ABEND S0C7\n%s", file, lines));
		}
	}

	private static Pattern JOB_ID_PATTERN = Pattern.compile(".*(JOB\\d{5}).*", Pattern.DOTALL);
	
	private static String getJobId(String reply) {
		String jobId = "";
		Matcher matcher = JOB_ID_PATTERN.matcher(reply);
		if (matcher.matches()) {
			jobId = matcher.group(1);
		}
		return jobId;
	}

	private static Random random = new Random();
	private static String get6DigitNumber() {
		return String.format("%06d", random.nextInt(999999));
	}
}
