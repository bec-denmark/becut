package dk.bec.unittest.becut.recorder.model;

import java.util.ArrayList;
import java.util.List;

public class SessionRecord {
	
	private Integer level;
	private String compileUnit;
	private String name;
	private String value;
	private SessionRecord parent;
	private List<SessionRecord> children = new ArrayList<SessionRecord>();

	public SessionRecord(Integer level, String compileUnit, String name, String value, SessionRecord parent,
			List<SessionRecord> children) {
		this.level = level;
		this.compileUnit = compileUnit;
		this.name = name;
		this.value = value;
		this.parent = parent;
		this.children = children;
	}
	
	public SessionRecord(String line) {
		String[] records = line.split("of");
		for (int i = 0; i < records.length; i++) {
			String[] parts = records[i].trim().split("\\s+");
			if (i == 0) {
				level = Integer.parseInt(parts[0]);
				String[] subparts = parts[1].split(":>");
				compileUnit = subparts[0];
				name = subparts[1];
			}
			if (parts.length == 4) {
				value = parts[3];
			}
		}
	}

	public Integer getLevel() {
		return level;
	}

	public String getCompileUnit() {
		return compileUnit;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public SessionRecord getParent() {
		return parent;
	}

	public void setParent(SessionRecord parent) {
		this.parent = parent;
	}

	public List<SessionRecord> getChildren() {
		return children;
	}
	
	public String fullyQualifiedName() {
		String fqn = name;
		SessionRecord currentRecord = this;
		while (currentRecord.parent != null) {
			currentRecord = currentRecord.parent;
			fqn += "_" + currentRecord.name;
		}
		return fqn;
	}
	
	@Override
	public String toString() {
		return level + " " + compileUnit + ":>" + name + " = " + value;
	}

}
