package dk.bec.unittest.becut.ftp;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileEntryParser;
import org.apache.commons.net.ftp.parser.FTPFileEntryParserFactory;
import org.apache.commons.net.ftp.parser.ParserInitializationException;

import dk.bec.unittest.becut.ftp.model.HostJob;
import dk.bec.unittest.becut.ftp.model.HostJobStatus;
import dk.bec.unittest.becut.ftp.model.JESFTPDataset;

public class HostFTPFileEntryParserFactory implements FTPFileEntryParserFactory {
	
	private static final String UNFINISHED_JOB_IDENTIFIER = "]{]{{?+#{{";
	
	private HostJob job;
	
	private boolean jobNext = false;
	private boolean jobRead = true;
	private boolean dsRead = false;
	private boolean jobFinished = false;

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
						job = new HostJob();
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
			//There are no files created while job is on input queue, so we cheat a little with a fake file
			if (!jobFinished && job.getStatus().equals(HostJobStatus.INPUT)) {
				jobFinished = true;
				return UNFINISHED_JOB_IDENTIFIER;
			}
			resetState();
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

			if (!UNFINISHED_JOB_IDENTIFIER.equals(line)) {
				//Zos specific
				ds.setId(Integer.parseInt(line.substring(9, 12)));
				ds.setStepname(line.substring(13, 21).trim());
				ds.setProcstep(line.substring(23, 31).trim());
				ds.setDsClass(line.substring(31, 32).trim());
				ds.setDdname(line.substring(33, 41).trim());
				String bytes = line.substring(42, 50).trim();
				ds.setBytes(Integer.parseInt(bytes));
				ds.setName(job.getName() + "::" + ds.getDdname());
				ds.containsDD(true);
			} else {
				ds.setName(job.getName());
				ds.containsDD(false);
			}
			
			
			ds.setJob(job);
			
			//General ftpfile
			ds.setRawListing(line);
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
	
	private void resetState() {
		jobNext = false;
		jobRead = true;
		dsRead = false;
		jobFinished = false;
	}
	
}
