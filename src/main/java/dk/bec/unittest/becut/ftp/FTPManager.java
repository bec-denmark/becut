package dk.bec.unittest.becut.ftp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import dk.bec.unittest.becut.Settings;
import dk.bec.unittest.becut.ftp.model.Credential;
import dk.bec.unittest.becut.ftp.model.DatasetProperties;
import dk.bec.unittest.becut.ftp.model.HostJob;
import dk.bec.unittest.becut.ftp.model.HostJobDataset;
import dk.bec.unittest.becut.ftp.model.HostJobStatus;
import dk.bec.unittest.becut.ftp.model.JESFTPDataset;

public class FTPManager {
	
	private static Pattern JOB_ID_PATTERN = Pattern.compile(".*(JOB\\d{5}).*", Pattern.DOTALL);
	
	//Enforce non-instantiation
	private FTPManager() {}

	public static void connectAndLogin(FTPClient ftp, Credential credential) throws Exception {
		ftp.connect(credential.getHost());
		if (!ftp.getReplyString().substring(0, 3).equals("220")) {
			throw new Exception(ftp.getReplyString());
		}
		ftp.enterLocalPassiveMode();
		ftp.login(credential.getUsername(), credential.getPassword());
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
	
	public static Boolean allocateDataset(FTPClient ftp, String datasetName, DatasetProperties datasetProperties) throws Exception {
		setUp(ftp, datasetProperties);
		return ftp.storeFile("'" + datasetName + "'", new ByteArrayInputStream("".getBytes()));
	}
	
	public static Boolean deleteMember(FTPClient ftp, String datasetName) throws Exception {
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
	
	public static HostJob getJob(FTPClient ftpClient, String jobId, boolean downloadContent) throws Exception {
		HostJob job = new HostJob();
		job.setId(jobId);
		
		JESFTPDataset[] datasets = listJES(ftpClient, jobId);
		if (datasets.length > 0) {
			job = datasets[0].getJob();
			//We have a job without DD cards, so we return the job info only
			if (!datasets[0].containsDD()) {
				return job;
			}
		}
		
		//https://www.ibm.com/support/knowledgecenter/SSLTBW_2.1.0/com.ibm.zos.v2r1.halu001/intfjesrecspoolgroup.htm
		List<String> files = new ArrayList<>();
		if(downloadContent) {
//			//FIXME hack to avoid fetching spools one by one 
//			//list return spools in a different order than get job.X
//			//they seem to be sorted by ddname and than by stepname
			Arrays.sort(datasets, (sd1, sd2) -> {
				int result = sd1.getStepname().compareTo(sd2.getStepname());
				if(result != 0) return result;
				return sd1.getDdname().compareTo(sd2.getDdname());
			});
			
			String allSpooFiles = retrieveJESDataset(ftpClient, jobId, "X");
			StringBuilder sb = new StringBuilder(); 
			Arrays.asList(allSpooFiles.split("\\r?\\n"))
			.forEach(line -> {
				if(line.contains("END OF JES SPOOL FILE")) {
					files.add(sb.toString());
					sb.delete(0, sb.length());
				} else { 
					sb.append(line.substring(1));
					sb.append("\n");
				}
			});			
		}
		
		for (int i = 0; i < datasets.length; i++) {
			HostJobDataset jobDataset = datasets[i].toJobDataset();
			if (downloadContent) {
				jobDataset.setContents(files.get(i));
				//jobDataset.setContents(retrieveJESDataset(ftpClient, jobDataset));
			}
			job.getDatasets().put(datasets[i].getDdname(), jobDataset);
		}
		return job;		
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
		
//		JESFTPDataset[] jesftpDatasets = Arrays.copyOf(files, files.length, JESFTPDataset[].class);
//		//FIXME hack to avoid fetching spools one by one 
//		//list return spools in a different order than get job.X
//		//they seem to be sorted by ddname and than by stepname
//		Arrays.sort(jesftpDatasets, (sd1, sd2) -> {
//			int result = sd1.getDdname().compareTo(sd2.getDdname());
//			if(result != 0) return result;
//			return sd1.getStepname().compareTo(sd2.getStepname());
//		});
//		
//		return jesftpDatasets;
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
		//FIXME should be set by -Dfile.encoding=Cp1252 but somewhere it is set to UTF-8
		return outputStream.toString("Cp1252");
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
			throw new RuntimeException(e);
		}
	}
	
	public static HostJob submitJobAndWaitToComplete(FTPClient ftp, InputStream jcl, Integer waitInSeconds, Boolean downloadContent) throws Exception {
		String jobId = submitJob(ftp, jcl);
		boolean jobCompleted = false;
		
		for (int i = 0; i < Math.ceil(waitInSeconds.doubleValue()/Settings.JOB_POLLING_RATE); i++) {
			HostJob hostJob = FTPManager.getJob(ftp, jobId, false);
			if (hostJob.getStatus().equals(HostJobStatus.OUTPUT)) {
				jobCompleted = true;
				break;
			}
			Thread.sleep(Settings.JOB_POLLING_RATE * 1000);
		}
		
		//We can't download the content if the job never finished
		if (!jobCompleted) {
			downloadContent = false;
		}
		
		return getJob(ftp, jobId, downloadContent);
	}
	
	public static HostJob submitJobAndWaitToComplete(FTPClient ftp, File jcl, Integer waitInSeconds, Boolean downloadContent) throws Exception {
		return submitJobAndWaitToComplete(ftp, new FileInputStream(jcl), waitInSeconds, downloadContent);
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
	
}
