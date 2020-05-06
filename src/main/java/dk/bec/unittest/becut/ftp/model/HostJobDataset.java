package dk.bec.unittest.becut.ftp.model;

public class HostJobDataset {
	
	private HostJob job;
	private Integer id;
	private String ddname;
	private String stepname;
	private String procstep;
	private Integer bytes;
	private String contents;
	private String dsClass;

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
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public String getDsClass() {
		return dsClass;
	}
	public void setDsClass(String dsClass) {
		this.dsClass = dsClass;
	}

}
