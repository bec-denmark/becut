package dk.bec.unittest.becut.ftp.model;

import org.apache.commons.net.ftp.FTPFile;

public class JESFTPDataset extends FTPFile {

	private static final long serialVersionUID = 5839355054714137601L;

	private HostJob job;
	private Integer id;
	private String ddname;
	private String stepname;
	private String procstep;
	private Integer bytes;
	private String dsClass;
	
	public HostJobDataset toJobDataset() {
		HostJobDataset jobDataset = new HostJobDataset();
		jobDataset.setJob(job);
		jobDataset.setId(id);
		jobDataset.setDdname(ddname);
		jobDataset.setStepname(stepname);
		jobDataset.setProcstep(procstep);
		jobDataset.setBytes(bytes);
		jobDataset.setDsClass(dsClass);
		
		return jobDataset;
	}

	public HostJob getJob() {
		return job;
	}

	public void setJob(HostJob job) {
		this.job = job;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDdname() {
		return ddname;
	}

	public void setDdname(String ddname) {
		this.ddname = ddname;
	}

	public String getStepname() {
		return stepname;
	}

	public void setStepname(String stepname) {
		this.stepname = stepname;
	}

	public String getProcstep() {
		return procstep;
	}

	public void setProcstep(String procstep) {
		this.procstep = procstep;
	}

	public Integer getBytes() {
		return bytes;
	}

	public void setBytes(Integer bytes) {
		this.bytes = bytes;
	}

	public String getDsClass() {
		return dsClass;
	}

	public void setDsClass(String dsClass) {
		this.dsClass = dsClass;
	}
	
	@Override
	public String toString() {
		return this.getName();
	}

}
