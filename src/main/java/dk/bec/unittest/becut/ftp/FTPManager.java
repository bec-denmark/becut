package dk.bec.unittest.becut.ftp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileEntryParser;
import org.apache.commons.net.ftp.parser.FTPFileEntryParserFactory;
import org.apache.commons.net.ftp.parser.ParserInitializationException;

import dk.bec.unittest.becut.Settings;
import dk.bec.unittest.becut.ftp.model.DatasetProperties;
import dk.bec.unittest.becut.ftp.model.HostJob;
import dk.bec.unittest.becut.ftp.model.HostJobDataset;
import dk.bec.unittest.becut.ftp.model.HostJobStatus;
import dk.bec.unittest.becut.ftp.model.JESFTPDataset;
import dk.bec.unittest.becut.ftp.model.SequentialDatasetProperties;

public class FTPManager {
	
	private static Pattern JOB_ID_PATTERN = Pattern.compile(".*(JOB\\d{5}).*", Pattern.DOTALL);
	
	//Enforce non-instantiation
	private FTPManager() {}

	public static void connectAndLogin(FTPClient ftp, String userId, String password) throws Exception {
		
		ftp.connect(Settings.FTP_HOST);
		if (!ftp.getReplyString().substring(0, 3).equals("220")) {
			throw new Exception(ftp.getReplyString());
		}
		ftp.enterLocalPassiveMode();
		ftp.login(userId, password);
		if (!ftp.getReplyString().substring(0, 3).equals("230")) {
			throw new Exception(ftp.getReplyString());
		}
	}

	public static byte[] retrieveVBMember(FTPClient ftp, String datasetName) throws Exception {
		ftp.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
		ftp.site("rdw");
		if (!ftp.getReplyString().substring(0, 3).equals("200")) {
			throw new Exception(ftp.getReplyString());
		}
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(Settings.OUTPUTSTREAM_BUFFER_INITIAL_CAPACITY);
		ftp.retrieveFile("'" + datasetName + "'", outputStream);
		if (!ftp.getReplyString().substring(0, 3).equals("250")) {
			throw new Exception(ftp.getReplyString());
		}
		outputStream.close();
		return outputStream.toByteArray();
	}

	public static String retrieveMember(FTPClient ftp, String datasetName) throws Exception {
		ftp.site("filetype=seq");
		if (!ftp.getReplyString().substring(0, 3).equals("200")) {
			throw new Exception(ftp.getReplyString());
		}
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(Settings.OUTPUTSTREAM_BUFFER_INITIAL_CAPACITY);
		ftp.retrieveFile("'" + datasetName + "'", outputStream);
		if (!ftp.getReplyString().substring(0, 3).equals("250")) {
			throw new Exception(ftp.getReplyString());
		}
		outputStream.close();
		return outputStream.toString();
	}
	
	public static Boolean sendDataset(FTPClient ftp, String datasetName, File file, DatasetProperties datasetProperties) throws Exception {
		setUp(ftp, datasetProperties);
		return ftp.storeFile("'" + datasetName + "'", new FileInputStream(file));
	}
	
	public static Boolean deleteMember(FTPClient ftp, String datasetName, DatasetProperties datasetProperties) throws Exception {
		ftp.site("filetype=seq");
		if (!ftp.getReplyString().substring(0, 3).equals("200")) {
			throw new Exception(ftp.getReplyString());
		}
		Boolean result = ftp.deleteFile("'" + datasetName + "'");
		if (!result) {
			throw new Exception("FTP delete command failed for deleting dataset: " + datasetName);
		}
		return result;
	}
	
	public static JESFTPDataset[] listJES(FTPClient ftp, String jobId) throws Exception {
		ftp.setParserFactory(new HostFTPFileEntryParserFactory());
		ftp.site("FILETYPE=JES");
		ftp.site("JESOWNER=*");
		ftp.site("JESJOBNAME=*");
		FTPFile[] files = ftp.listFiles(jobId);
		if (!ftp.getReplyString().substring(0, 3).equals("250")) {
			throw new Exception(ftp.getReplyString());
		}
		return Arrays.copyOf(files, files.length, JESFTPDataset[].class);
	}
	
	public static String retrieveJESAllDatasets(FTPClient ftp, String jobId) throws Exception {
		ftp.site("FILETYPE=JES");
		ftp.site("JESOWNER=*");
		ftp.site("JESJOBNAME=*");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(Settings.OUTPUTSTREAM_BUFFER_INITIAL_CAPACITY);
		ftp.retrieveFile(jobId, outputStream);
		if (!ftp.getReplyString().substring(0, 3).equals("250")) {
			throw new Exception(ftp.getReplyString());
		}
		outputStream.close();
		return outputStream.toString();
	}
	
	public static String retrieveJESDataset(FTPClient ftp, HostJobDataset dataset) throws Exception {
		return retrieveJESDataset(ftp, dataset.getJob().getId(), dataset.getId().toString());
	}
	
	public static String retrieveJESDataset(FTPClient ftp, String jobId, String datasetID) throws Exception {
		ftp.site("FILETYPE=JES");
		ftp.site("JESOWNER=*");
		ftp.site("JESJOBNAME=*");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(Settings.OUTPUTSTREAM_BUFFER_INITIAL_CAPACITY);
		ftp.retrieveFile(jobId + "." + datasetID, outputStream);
		if (!ftp.getReplyString().substring(0, 3).equals("250")) {
			throw new Exception(ftp.getReplyString());
		}
		outputStream.close();
		return outputStream.toString();
	}
	
	public static String submitJob(FTPClient ftp, InputStream jcl) {
		String jobId = "";
		try {
			ftp.site("FILETYPE=JES");
			ftp.storeFile("TNONAME0", jcl);
			String reply = ftp.getReplyString();
			jobId = getJobId(reply);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jobId;
	}
	
	public static String submitJob(FTPClient ftp, File jcl) {
		try {
			return submitJob(ftp, new FileInputStream(jcl));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	private static void setUp(FTPClient ftp, DatasetProperties datasetProperties) throws Exception {
		for (String siteCommand: datasetProperties.getFTPSiteCommands()) {
			ftp.sendSiteCommand(siteCommand);
			if (!ftp.getReplyString().substring(0, 3).equals("200")) {
				throw new Exception(ftp.getReplyString());
			}
		}
	}
	
	private static String getJobId(String reply) {
		String jobId = "";
		Matcher matcher = JOB_ID_PATTERN.matcher(reply);
		if (matcher.matches()) {
			jobId = matcher.group(1);
		}
		return jobId;
	}
	
	private static class HostFTPFileEntryParserFactory implements FTPFileEntryParserFactory {
		
		private HostJob job;
		
		private boolean jobNext = false;
		private boolean jobRead = true;
		private boolean dsRead = false;

		private FTPFileEntryParser parser = new FTPFileEntryParser() {
			
			@Override
			public String readNextEntry(BufferedReader reader) throws IOException {
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.contains("spool file")) {
						dsRead = false;
					}
					if (jobRead) {
						if (jobNext) {
							String[] jobParts = line.split("\\s+");
							job = new HostJob();
							job.setName(jobParts[0]);
							job.setId(jobParts[1]);
							job.setOwner(jobParts[2]);
							job.setStatus(HostJobStatus.valueOf(jobParts[3]));
							job.setJobClass(jobParts[4]);
							//No return code if job isn't finished
							if (jobParts.length > 5) {
								job.setReturnCode(jobParts[5]);
							} else {
								job.setReturnCode("Job still running");
							}
							jobNext = false;
							jobRead = false;
						}
						if (line.startsWith("JOBNAME" )) {
							jobNext = true;
							continue;
						}
					}
					if (line.trim().startsWith("ID")) {
						dsRead = true;
						continue;
					}
					if (dsRead) {
						return line;
					}
				}
				return null;
			}
			
			@Override
			public List<String> preParse(List<String> arg0) {
				return null;
			}
			
			@Override
			public FTPFile parseFTPEntry(String line) {
				//TODO This might look different on different installations
				JESFTPDataset ds = new JESFTPDataset();
				//Zos specific
				ds.setId(Integer.parseInt(line.substring(9, 12)));
				ds.setStepname(line.substring(13, 21).trim());
				ds.setProcstep(line.substring(23, 31).trim());
				ds.setDsClass(line.substring(31, 32).trim());
				ds.setDdname(line.substring(33, 41).trim());
				String bytes = line.substring(42, 50).trim();
				ds.setBytes(Integer.parseInt(bytes));
				ds.setJob(job);
				
				//General ftpfile
				ds.setRawListing(line);
				ds.setName(job.getName() + "::" + ds.getDdname());
				ds.setType(FTPFile.FILE_TYPE);
				ds.setUser(job.getOwner());
				
				
				return ds;
			}
		};

		@Override
		public FTPFileEntryParser createFileEntryParser(String arg0) throws ParserInitializationException {
			// TODO Auto-generated method stub
			return parser;
		}

		@Override
		public FTPFileEntryParser createFileEntryParser(FTPClientConfig arg0) throws ParserInitializationException {
			// TODO Auto-generated method stub
			return parser;
		}
		
	}
	

}
