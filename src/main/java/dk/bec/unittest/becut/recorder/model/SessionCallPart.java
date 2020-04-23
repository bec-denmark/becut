package dk.bec.unittest.becut.recorder.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.bec.unittest.becut.recorder.DebugToolLogParser;


public class SessionCallPart {

	private List<SessionRecord> records = new ArrayList<SessionRecord>();
	
	private Map<String, SessionRecord> sessionRecordMap = new HashMap<String, SessionRecord>();
	
	public SessionCallPart(List<String> callBlock) {
		int i = 0;
		while (!(callBlock.get(i).startsWith(DebugToolLogParser.END_CALL_MARKER) || callBlock.get(i).startsWith(DebugToolLogParser.END_AFTER_CALL_MARKER))) {
			if (callBlock.get(i).matches("^\\d{2}.*$")) {
				SessionRecord sessionRecord = new SessionRecord(callBlock.get(i));
				if (sessionRecord.getLevel() == 1) {
					records.add(sessionRecord);
					sessionRecordMap.put(sessionRecord.fullyQualifiedName(), sessionRecord);
				} else {
					linkRecords(callBlock.get(i), sessionRecord);
				}
			}
			i++;
		}
	}
	
	public List<SessionRecord> getRecords() {
		return records;
	}

	private void linkRecords(String line, SessionRecord sessionRecord) {
		String[] recs = line.split("of");
		SessionRecord decendent = null;
		//We start at 1 because we have the sessionRecord as a parameter
		for (int i = 1; i < recs.length; i++) {
			SessionRecord tempSessionRecord = new SessionRecord(recs[i]);
			if (!sessionRecordMap.containsKey(tempSessionRecord.fullyQualifiedName())) {
				sessionRecordMap.put(tempSessionRecord.fullyQualifiedName(), tempSessionRecord);
			}
			SessionRecord ancestor = sessionRecordMap.get(tempSessionRecord.fullyQualifiedName());
			if (sessionRecord.getLevel() - 1 == ancestor.getLevel()) {
				sessionRecord.setParent(ancestor);
				ancestor.getChildren().add(sessionRecord);
			}
			if (decendent != null && ancestor.getLevel() + 1 == decendent.getLevel()) {
				decendent.setParent(ancestor);
				if (!ancestor.getChildren().contains(decendent)) {
					ancestor.getChildren().add(decendent);
				}
			}
			if (ancestor.getLevel() == 1 && !records.contains(ancestor)) {
				records.add(ancestor);
			}
			decendent = ancestor;
		}	
	}
}
