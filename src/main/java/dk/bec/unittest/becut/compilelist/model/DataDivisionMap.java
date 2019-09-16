package dk.bec.unittest.becut.compilelist.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DataDivisionMap extends AbstractCompileListingSection {
	
	private Map<Integer, Record> records = new TreeMap<>();
	
	public DataDivisionMap(List<String> lines) {
		originalSource = lines;
		
		int startOfRecord = 0;
		int endOfRecord = 0;
		int count = 0;
		List<List<String>> linesGroupedByRecord = new ArrayList<List<String>>();
		boolean first = true;
		for (String line: lines) {
			Integer level = getLevel(line);
			if (level == -1) {
				count++;
				continue;
			}
			if (level == 1 || level == 77) {
				if (first) {
					startOfRecord = count;
					first = false;
				}
				else {
					endOfRecord = count;
					linesGroupedByRecord.add(lines.subList(startOfRecord, endOfRecord));
					startOfRecord = endOfRecord;
				}
			}
			count++;
		}
		linesGroupedByRecord.add(lines.subList(startOfRecord, lines.size()));
		for (List<String> recordLines: linesGroupedByRecord) {
			List<Record> r= new ArrayList<Record>(recordLines.size());
			for (String line: recordLines) {
				r.add(new Record(line));
			}
			Record.groupToRecord(r);
			records.put(r.get(0).getLineNumber(), r.get(0));
		}
	}
	
	public Map<Integer, Record> getRecords() {
		return records;
	}

	public void setRecords(Map<Integer, Record> records) {
		this.records = records;
	}
	
	/*
	 * Note: There may be multiple records with this name
	 */
	public List<Record> getRecord(String recordName) {
		return getRecord(recordName, records.values());
	}
	
	private List<Record> getRecord(String recordName, Collection<Record> rs) {
		List<Record> matches = new ArrayList<Record>();
		for (Record r: rs) {
			if (recordName.equalsIgnoreCase(r.getName())) {
				matches.add(r);
			}
			matches.addAll(getRecord(recordName, r.getSubRecords().values()));
		}
		return matches;
	}
	
	public Record getRecord(Integer lineNumber) {
		return getRecord(lineNumber, records.values());
	}
	
	private Record getRecord(Integer lineNumber, Collection<Record> rs) {
		for (Record r: rs) {
			if (lineNumber.equals(r.getLineNumber())) {
				return r;
			}
			Record found = getRecord(lineNumber, r.getSubRecords().values());
			if (found != null) {
				return found;
			}
		}
		return null;
	}
	
	private Integer getLevel(String line) {
		Integer i = -1;
		line = line.replaceAll("\\.", "").trim();
		String[] parts = line.split("\\s+");
		try {
			i = Integer.parseInt(parts[1]);
		}
		catch (NumberFormatException e) {
		}
		return i;
	}

}
