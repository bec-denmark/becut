package dk.bec.unittest.becut.compilelist.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.bec.unittest.becut.compilelist.model.DataNameReference.DataReference;

public class DataNamesCrossReference extends AbstractCompileListingSection {
	
	Map<Integer, DataNameReference> dataNameReferencesByLineNumber = new HashMap<>();
	//Names can be the same
	Map<String, List<DataNameReference>> dataNameReferencesByName = new HashMap<>();
	
	public DataNamesCrossReference(List<String> lines) {
		originalSource = lines;
		
		Integer lineNumber = -1;
		String dataName = "";
		List<DataReference> references = new ArrayList<DataReference>();
		Integer lastSeenReferenceNumber = null;
		
		for (String line: lines) {
			String l = line.trim();
			String[] parts = l.split("\\s+");
			if (parts.length > 0) {
				if (!l.contains(".")) {
					//We have XREFS(FULL) and no references, so we skip it
					if (l.length() < 44) {
						continue;
					}
					DataNameReference lastSeenReference = dataNameReferencesByLineNumber.get(lastSeenReferenceNumber);
					for (int i = 0; i < parts.length; i++) {
						Boolean modified = false;
						if (parts[i].matches("(\\d+|M\\d+)")) {
							if (parts[i].startsWith("M")) {
								modified = true;
								parts[i] = parts[i].substring(1);
							}
							references.add(new DataReference(Integer.parseInt(parts[i]), modified));
						}
					}
					lastSeenReference.getReferences().addAll(references);
				}
				else {
					lineNumber = Integer.parseInt(parts[0]);
					if (parts[1].endsWith(".")) {
						dataName = parts[1].substring(0, parts[1].length() - 1);
					}
					else {
						dataName = parts[1];
					}
					for (int i = 2; i < parts.length; i++) {
						Boolean modified = false;
						if (parts[i].matches("(\\d+|M\\d+)")) {
							if (parts[i].startsWith("M")) {
								modified = true;
								parts[i] = parts[i].substring(1);
							}
							references.add(new DataReference(Integer.parseInt(parts[i]), modified));
						}
					}
					DataNameReference reference = new DataNameReference(lineNumber, dataName, references);
					dataNameReferencesByLineNumber.put(lineNumber, reference);
					if (dataNameReferencesByName.containsKey(dataName)) {
						dataNameReferencesByName.get(dataName).add(reference);
					}
					else {
						List<DataNameReference> dnr = new ArrayList<>();
						dnr.add(reference);
						dataNameReferencesByName.put(dataName, dnr);
					}
					lastSeenReferenceNumber = lineNumber;
				}
			}
			references = new ArrayList<>();
		}	
	}

	public Map<Integer, DataNameReference> getDataNameReferencesByLineNumber() {
		return dataNameReferencesByLineNumber;
	}

	public void setDataNameReferencesByLineNumber(Map<Integer, DataNameReference> dataNameReferencesByLineNumber) {
		this.dataNameReferencesByLineNumber = dataNameReferencesByLineNumber;
	}

	public Map<String, List<DataNameReference>> getDataNameReferencesByName() {
		return dataNameReferencesByName;
	}

	public void setDataNameReferencesByName(Map<String, List<DataNameReference>> dataNameReferencesByName) {
		this.dataNameReferencesByName = dataNameReferencesByName;
	}

}
