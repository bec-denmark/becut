package dk.bec.unittest.becut.ftp.model;

import java.util.HashMap;
import java.util.Map;

public class HostJob {

	private String name;
	private String id;
	private String owner;
	private HostJobStatus status = HostJobStatus.UNKNOWN;
	private String jobClass;
	private String returnCode;
	private Map<String, HostJobDataset> datasets = new HashMap<String, HostJobDataset>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public HostJobStatus getStatus() {
		return status;
	}

	public void setStatus(HostJobStatus status) {
		this.status = status;
	}

	public String getJobClass() {
		return jobClass;
	}

	public void setJobClass(String jobClass) {
		this.jobClass = jobClass;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public Map<String, HostJobDataset> getDatasets() {
		return datasets;
	}

	public void setDatasets(Map<String, HostJobDataset> datasets) {
		this.datasets = datasets;
	}

}
