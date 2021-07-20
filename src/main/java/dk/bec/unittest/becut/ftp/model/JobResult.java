package dk.bec.unittest.becut.ftp.model;

import java.util.List;

public class JobResult {
	public List<String> spool;
	public String rc;
	
	public JobResult(String rc, List<String> spool) {
		this.rc = rc;
		this.spool = spool;
	}
}
