package dk.bec.unittest.becut.ftp.model;

import java.util.ArrayList;
import java.util.List;

public class SequentialDatasetProperties implements DatasetProperties {
	
	private static final String FILETYPE = "SEQ";
	private RecordFormat recordFormat = RecordFormat.FIXED_BLOCK;
	private Integer recordLength = 0;
	private Integer blockSize = 0;
	private String volume = "";
	private String genericUnit = "";
	private SpaceUnits spaceUnits = SpaceUnits.NOT_SPECIFIED;
	private Integer primaryQuantity = 0;
	private Integer secondaryQuantity = 0;
	
	public SequentialDatasetProperties(RecordFormat recordFormat, Integer recordLength) {
		this.recordFormat = recordFormat;
		this.recordLength = recordLength;
	}

	public SequentialDatasetProperties(RecordFormat recordFormat, Integer recordLength, Integer blockSize,
			String volume, String genericUnit, SpaceUnits spaceUnits, Integer primaryQuantity,
			Integer secondaryQuantity) {
		this.recordFormat = recordFormat;
		this.recordLength = recordLength;
		this.blockSize = blockSize;
		this.volume = volume;
		this.genericUnit = genericUnit;
		this.spaceUnits = spaceUnits;
		this.primaryQuantity = primaryQuantity;
		this.secondaryQuantity = secondaryQuantity;
	}
	
	public List<String> getFTPSiteCommands() {
		List<String> commands = new ArrayList<>(9);
		commands.add("filetype=" + FILETYPE);
		commands.add("recfm=" + recordFormat.getFormat());
		commands.add("lrecl=" + recordLength);
		
		if (blockSize > 0) {
			commands.add("blocksize=" + blockSize);
		}
		
		if (!volume.isEmpty()) {
			commands.add("volume=" + volume);
		}
		
		if (!genericUnit.isEmpty()) {
			commands.add("unit=" + genericUnit);
		}
		
		if (spaceUnits == SpaceUnits.NOT_SPECIFIED) {
			commands.add(spaceUnits.toString());
			
		}
		
		if (primaryQuantity > 0) {
			commands.add("primary=" + primaryQuantity);
		}
		
		if (secondaryQuantity > 0) {
			commands.add("secondary=" + secondaryQuantity);
		}
		return commands;
	}

	public RecordFormat getRecordFormat() {
		return recordFormat;
	}

	public void setRecordFormat(RecordFormat recordFormat) {
		this.recordFormat = recordFormat;
	}

	public Integer getRecordLength() {
		return recordLength;
	}

	public void setRecordLength(Integer recordLength) {
		this.recordLength = recordLength;
	}

	public Integer getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(Integer blockSize) {
		this.blockSize = blockSize;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getGenericUnit() {
		return genericUnit;
	}

	public void setGenericUnit(String genericUnit) {
		this.genericUnit = genericUnit;
	}

	public SpaceUnits getSpaceUnits() {
		return spaceUnits;
	}

	public void setSpaceUnits(SpaceUnits spaceUnits) {
		this.spaceUnits = spaceUnits;
	}

	public Integer getPrimaryQuantity() {
		return primaryQuantity;
	}

	public void setPrimaryQuantity(Integer primaryQuantity) {
		this.primaryQuantity = primaryQuantity;
	}

	public Integer getSecondaryQuantity() {
		return secondaryQuantity;
	}

	public void setSecondaryQuantity(Integer secondaryQuantity) {
		this.secondaryQuantity = secondaryQuantity;
	}
	
}
