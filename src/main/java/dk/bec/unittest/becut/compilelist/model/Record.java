package dk.bec.unittest.becut.compilelist.model;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;


	/* 
	 * This record information is taken from the Data Division Map.
	 * It takes the whole data division map and makes a recursive structure of the record
	 */
public class Record {

	private Integer lineNumber = -1;
	private Integer level = -1;
	private String name = "";
	private DataType dataType = DataType.UNKNOWN;
	private Integer size = 0;
	private Map<Integer, Record> subRecords = new TreeMap<>();
	private Record parent = null;
	private Boolean isSeventySeven = Boolean.FALSE;
	
	public Record(String line) {
		line = line.trim().replaceAll("\\.", "");
		String[] parts = line.split("\\s+");
		this.lineNumber = Integer.parseInt(parts[0]);
		if(parts[1].trim().equals("FD")) {
			this.level = 1;
			Record p = null;
			this.parent = p;
			this.name = parts[2];
		} else {
			this.level = Integer.parseInt(parts[1]);
			Record p = null;
			this.parent = p;
			this.name = parts[2];
			switch (this.level) {
				case 88:
					this.dataType = DataType.EIGHTYEIGHT;
					this.size = -1;
					break;
				case 77:
					this.size = parseSize(parts[6]);
					this.dataType = DataType.parseDataType(parts[7]);
					this.isSeventySeven = Boolean.TRUE;
					break;
				default:
					if (parts.length < 10) {
						this.size = parseSize(parts[6]);
						this.dataType = DataType.parseDataType(parts[7]);
					}
					else {
						this.size = parseSize(parts[9]);
						this.dataType = DataType.parseDataType(parts[10]);
					}
					break;
			}
		}
	}

	private Integer parseSize(String dataInfo) {
		Integer i = -1;
		if (dataInfo.contains("L")) {
			i = Integer.parseInt(dataInfo.substring(dataInfo.indexOf("L") + 1, dataInfo.length()));
		}
		else {
			if (dataInfo.indexOf("P") != -1) {
				i = Integer.parseInt(dataInfo.substring(0, dataInfo.indexOf("P")));
			}
			else if (dataInfo.indexOf("C") != -1) {
				i = Integer.parseInt(dataInfo.substring(0, dataInfo.indexOf("C")));
			}
		}
		return i;
	}

	public Integer getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Map<Integer, Record> getSubRecords() {
		return subRecords;
	}

	public void setSubRecords(Map<Integer, Record> subRecords) {
		this.subRecords = subRecords;
	}

	public Record getParent() {
		return parent;
	}

	public void setParent(Record parent) {
		this.parent = parent;
	}

	public Boolean getIsSeventySeven() {
		return isSeventySeven;
	}

	public void setIsSeventySeven(Boolean isSeventySeven) {
		this.isSeventySeven = isSeventySeven;
	}
	
	public static Record groupToRecord(List<Record> records) {
		if (records.size() < 1) {
			return null;
		}
		Record top = records.get(0);
		Record previousRecord = top;
		for (Record record: records.subList(1, records.size())) {
			if (previousRecord.getLevel() < record.getLevel()) {
				record.setParent(previousRecord);
				previousRecord.getSubRecords().put(record.getLineNumber(), record);
				previousRecord = record;
			} else {
				Record descendantRecord = previousRecord;
				while (descendantRecord.getParent() != null) {
					if (descendantRecord.getLevel().equals(record.getLevel())) {
						record.setParent(descendantRecord.getParent());
						descendantRecord.getParent().getSubRecords().put(record.getLineNumber(), record);
						previousRecord = record;
						break;
					}
					descendantRecord = descendantRecord.getParent();
				}
			}
			if (record.getDataType().equals(DataType.EIGHTYEIGHT)) {
				record.size = record.getParent().getSize();
			}
		}
		
		return top;
	}
	
	@Override
	public String toString() {
		String sub = this.subRecords.isEmpty() ? "" : this.subRecords.toString();
		return this.level + "  " + this.name + "\n" + sub;
	}

}
