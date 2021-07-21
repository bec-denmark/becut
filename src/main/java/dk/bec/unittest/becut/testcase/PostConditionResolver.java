package dk.bec.unittest.becut.testcase;

import java.util.ArrayList;
import java.util.List;

import dk.bec.unittest.becut.recorder.model.SessionPostCondition;
import dk.bec.unittest.becut.recorder.model.SessionRecord;
import dk.bec.unittest.becut.recorder.model.SessionRecording;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import dk.bec.unittest.becut.testcase.model.Parameter;
import dk.bec.unittest.becut.testcase.model.PostConditionComparison;
import dk.bec.unittest.becut.testcase.model.PostConditionResult;

public class PostConditionResolver {

	private PostConditionResolver() {}
	
	public static PostConditionResult verify(BecutTestCase becutTestCase, SessionRecording sessionRecording) {
		PostConditionResult postConditionResult = new PostConditionResult();
		//TODO add the other parts of the data division
		List<Parameter> postConditions = becutTestCase.getPostCondition().getWorkingStorage();
		
		List<SessionRecord> sessionRecords = new ArrayList<SessionRecord>();
		for (SessionPostCondition sessionPostCondition: sessionRecording.getSessionPostConditions()) {
			sessionRecords.addAll(sessionPostCondition.getSessionRecords());
		}
		//Find the matching parameter to the session record
		for (SessionRecord sessionRecord: sessionRecords) {
			for (Parameter parameter: postConditions) {
				Parameter currentParameter = traversParameter(parameter, sessionRecord);
				if (currentParameter != null) {
					PostConditionComparison postConditionComparison = new PostConditionComparison(currentParameter, sessionRecord);
					postConditionResult.getComparisons().add(postConditionComparison);
					break;
				}
			}
			
		}
		
		return postConditionResult;
	}
	
	private static Parameter traversParameter(Parameter parameter, SessionRecord sessionRecord) {
		Parameter result = null;
		if (parameter.getName().equals(sessionRecord.getName())) {
			return parameter;
		}
		for (Parameter p: parameter.getSubStructure()) {
			Parameter currentResult = traversParameter(p, sessionRecord);
			if (currentResult != null) {
				result = currentResult;
			}
		}
		return result;
	}
	
}
