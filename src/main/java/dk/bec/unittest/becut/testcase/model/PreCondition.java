package dk.bec.unittest.becut.testcase.model;

import java.util.ArrayList;
import java.util.List;

public class PreCondition {
	
	private List<Parameter> workingStorage = new ArrayList<Parameter>();
	private List<Parameter> localStorage = new ArrayList<Parameter>();
	private List<Parameter> linkageSection = new ArrayList<Parameter>();
	private List<Parameter> fileSection = new ArrayList<Parameter>();
	
	public List<Parameter> getWorkingStorage() {
		return workingStorage;
	}
	public void setWorkingStorage(List<Parameter> workingStorage) {
		this.workingStorage = workingStorage;
	}
	public void addWorkingStorageRecord(Parameter record) {
		workingStorage.add(record);
	}
	public List<Parameter> getLocalStorage() {
		return localStorage;
	}
	public void setLocalStorage(List<Parameter> localStorage) {
		this.localStorage = localStorage;
	}
	public List<Parameter> getLinkageSection() {
		return linkageSection;
	}
	public void setLinkageSection(List<Parameter> linkageSection) {
		this.linkageSection = linkageSection;
	}
	public List<Parameter> getFileSection() {
		return fileSection;
	}
	public void setFileSection(List<Parameter> fileSection) {
		this.fileSection = fileSection;
	}
}
