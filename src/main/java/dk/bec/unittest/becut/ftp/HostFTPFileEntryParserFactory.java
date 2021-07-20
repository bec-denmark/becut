package dk.bec.unittest.becut.ftp;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
	final FTPFileEntryParser parser = new FTPFileEntryParser() {
		boolean finished = false;
		HostJob job;
		@Override
		public FTPFile parseFTPEntry(String line) {
			if(finished) {
		        //         003 PGMEXEC     N/A   Q SYSOUT         407  
				if(line.matches("\\s+\\d\\d\\d.*")) {
					JESFTPDataset ds = new JESFTPDataset();

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
				
					ds.setJob(job);
					
					//General ftpfile
					ds.setRawListing(line);
					ds.setType(FTPFile.FILE_TYPE);
					ds.setUser(job.getOwner());
				
					return ds;
				}
			}
			return null;
		}

		@Override
		public List<String> preParse(List<String> lines) {
			//more than two lines - second line is complete
			if(lines.size() > 2) {
				String[] jobParts = lines.get(1).split("\\s+");
				if(jobParts[3].equals("OUTPUT")) {
					finished = true;
					job = new HostJob();
					job.setName(jobParts[0]);
					job.setId(jobParts[1]);
					job.setOwner(jobParts[2]);
					job.setStatus(HostJobStatus.valueOf(jobParts[3]));
					job.setJobClass(jobParts[4]);
					job.setReturnCode(jobParts[5]);
				} else if(jobParts[3].equals("HELD")) {
					throw new RuntimeException(String.format("The job %s is to be put on hold.", jobParts[1]));
				}
			}
			return lines;
		}

		@Override
		public String readNextEntry(BufferedReader br) throws IOException {
			return br.readLine();
		}
	};
	
	@Override
	public FTPFileEntryParser createFileEntryParser(String arg0) throws ParserInitializationException {
		return parser;
	}

	@Override
	public FTPFileEntryParser createFileEntryParser(FTPClientConfig arg0) throws ParserInitializationException {
		return parser;
	}
}
