package dk.bec.unittest.becut.ftp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import dk.bec.unittest.becut.DDNAME;
import dk.bec.unittest.becut.Settings;
import dk.bec.unittest.becut.ftp.model.Credential;
import dk.bec.unittest.becut.ftp.model.DatasetProperties;
import dk.bec.unittest.becut.ftp.model.HostJob;
import dk.bec.unittest.becut.ftp.model.HostJobDataset;
import dk.bec.unittest.becut.ftp.model.HostJobStatus;
import dk.bec.unittest.becut.ftp.model.JESFTPDataset;
import dk.bec.unittest.becut.ui.controller.ReturnCodeDifferentFromCC000;

public class FTPManager {
	
	private static Pattern JOB_ID_PATTERN = Pattern.compile(".*(JOB\\d{5}).*", Pattern.DOTALL);
	
	//Enforce non-instantiation
	private FTPManager() {}

	public static void connectAndLogin(FTPClient ftp, Credential credential) throws Exception {
		ftp.connect(credential.getHost());
		checkReply(ftp, "220");
		ftp.enterLocalPassiveMode();
		ftp.login(credential.getUsername(), credential.getPassword());
		checkReply(ftp, "230");
	}

	public static byte[] retrieveVBMember(FTPClient ftp, String datasetName) throws Exception {
		ftp.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
		ftp.site("rdw");
		checkReply(ftp, "200");
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(Settings.OUTPUTSTREAM_BUFFER_INITIAL_CAPACITY);
		retrieveFile(ftp, "'" + datasetName + "'", outputStream);
		outputStream.close();
		return outputStream.toByteArray();
	}

	public static String retrieveMember(FTPClient ftp, String datasetName) throws Exception {
		ftp.site("filetype=seq");
		checkReply(ftp, "200");
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(Settings.OUTPUTSTREAM_BUFFER_INITIAL_CAPACITY);
		retrieveFile(ftp, "'" + datasetName + "'", outputStream);
		outputStream.close();
		return outputStream.toString();
	}
	
	public static void sendDataset(FTPClient ftp, String datasetName, File file, DatasetProperties datasetProperties) {
		setUp(ftp, datasetProperties);
		storeFile(ftp, "'" + datasetName + "'", file);
	}
	
	public static void allocateDataset(FTPClient ftp, String datasetName, DatasetProperties datasetProperties) {
		setUp(ftp, datasetProperties);
		storeEmptyFile(ftp, "'" + datasetName + "'");
	}
	
	public static Boolean deleteMember(FTPClient ftp, String datasetName) throws Exception {
		ftp.site("filetype=seq");
		checkReply(ftp, "200");
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
		
		for (int i = 0; i < datasets.length; i++) {
			HostJobDataset jobDataset = datasets[i].toJobDataset();
			if (downloadContent) {
				//jobDataset.setContents(files.get(i));
				//jobDataset.setContents(retrieveJESDataset(ftpClient, jobDataset));
			}
			job.getDatasets().put(datasets[i].getDdname(), jobDataset);
		}
		
		if(downloadContent) {
			//downloading all dss takes time, get only those needed
			if(job.getReturnCode().equals("RC=0000")) {
				final HostJob finaljob = job;
				Arrays.asList(DDNAME.INSPLOG, DDNAME.SYSOUT)
					.forEach(dsName -> {
						HostJobDataset ds = finaljob.getDataset(dsName);
						if(ds != null) {
							ds.setContents(retrieveJESDataset(ftpClient, finaljob.getDataset(dsName)));
						}
					});
			} else {
				//get all DDs
				String dds = retrieveJESDataset(ftpClient, jobId,  "X");
				throw new ReturnCodeDifferentFromCC000(job.getReturnCode() +"\n" + dds);
			}
		}
		return job;		
	}
	
	public static JESFTPDataset[] listJES(FTPClient ftp, String jobId) throws Exception {
		ftp.setParserFactory(new HostFTPFileEntryParserFactory());
		ftp.site("FILETYPE=JES");
		ftp.site("JESOWNER=*");
		ftp.site("JESJOBNAME=*");
		FTPFile[] files = ftp.listFiles(jobId);
		checkReply(ftp, "250");
		return Arrays.copyOf(files, files.length, JESFTPDataset[].class);
	}
	
	public static String retrieveJESAllDatasets(FTPClient ftp, String jobId) throws Exception {
		ftp.site("FILETYPE=JES");
		ftp.site("JESOWNER=*");
		ftp.site("JESJOBNAME=*");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(Settings.OUTPUTSTREAM_BUFFER_INITIAL_CAPACITY);
		retrieveFile(ftp, jobId, outputStream);
		return outputStream.toString();
	}
	
	public static String retrieveJESDataset(FTPClient ftp, HostJobDataset dataset) {
		return retrieveJESDataset(ftp, dataset.getJob().getId(), dataset.getId().toString());
	}
	
	public static String retrieveJESDataset(FTPClient ftp, String jobId, String datasetID) {
		site(ftp, "FILETYPE=JES");
		site(ftp, "JESOWNER=*");
		site(ftp, "JESJOBNAME=*");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(Settings.OUTPUTSTREAM_BUFFER_INITIAL_CAPACITY);
		retrieveFile(ftp, jobId + "." + datasetID, outputStream);
		//FIXME should be set by -Dfile.encoding=Cp1252 but somewhere it is set to UTF-8
		try {
			return outputStream.toString("Cp1252");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String submitJob(FTPClient ftp, InputStream jcl) {
		String jobId = "";
		try {
			ftp.site("FILETYPE=JES");
			ftp.storeFile("TNONAME0", jcl);
			String reply = ftp.getReplyString();
			jobId = getJobId(reply);
		} catch (IOException e) {
			throw new RuntimeException(e);
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
		
		HostJob hostJob = null;
		for (int i = 0; i < Math.ceil(waitInSeconds.doubleValue()/Settings.JOB_POLLING_RATE); i++) {
			hostJob = FTPManager.getJob(ftp, jobId, false);
			if (hostJob.getStatus().equals(HostJobStatus.OUTPUT)) {
				jobCompleted = true;
				break;
			}
			Thread.sleep(Settings.JOB_POLLING_RATE * 1000);
		}
		
		hostJob = getJob(ftp, jobId, jobCompleted);
		
		return hostJob;
	}
	
	public static HostJob submitJobAndWaitToComplete(FTPClient ftp, File jcl, Integer waitInSeconds, Boolean downloadContent) throws Exception {
		return submitJobAndWaitToComplete(ftp, new FileInputStream(jcl), waitInSeconds, downloadContent);
	}
	
	private static void setUp(FTPClient ftp, DatasetProperties datasetProperties) {
		for (String siteCommand: datasetProperties.getFTPSiteCommands()) {
			sendSiteCommand(ftp, siteCommand);
			checkReply(ftp, "200");
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

	private static void site(FTPClient ftp, String parameters) {
		try {
			ftp.site(parameters);
			checkReply(ftp, "200");			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void retrieveFile(FTPClient ftp, String file, OutputStream os) {
		try {
			if(!ftp.retrieveFile(file, os)) {
				throw new FTPRetrieveFileException(replyStrings(ftp));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void storeFile(FTPClient ftp, String fileName, File file) {
		try {
			try(InputStream is = new FileInputStream(file)) {
				if(!ftp.storeFile(fileName, is)) {
					throw new FTPRetrieveFileException(replyStrings(ftp));
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void storeEmptyFile(FTPClient ftp, String fileName) {
		try {
			try(InputStream is = new ByteArrayInputStream(new byte[0])) {
				if(!ftp.storeFile(fileName, is)) {
					throw new FTPRetrieveFileException(replyStrings(ftp));
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void checkReply(FTPClient ftp, String status) {
		if (!ftp.getReplyString().substring(0, 3).equals(status)) {
			throw new RuntimeException(replyStrings(ftp));
		}
	}

	private static void sendSiteCommand(FTPClient ftp, String siteCommand) {
		try {
			if(!ftp.sendSiteCommand(siteCommand)) {
				throw new FTPRetrieveFileException(replyStrings(ftp));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static String replyStrings(FTPClient ftp) {
		return Arrays.asList(ftp.getReplyStrings())
				.stream()
				.collect(Collectors.joining(" "));		
	}
}
