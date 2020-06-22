package dk.bec.unittest.becut.testcase.model;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import dk.bec.unittest.becut.debugscript.model.CallType;

public class ExternalCall {

	private String name;
	private String displayableName;
	private Integer lineNumber;
	private CallType callType;

	@JsonSerialize(using = IterationsSerializer.class)
	@JsonDeserialize(using = IterationDeserializer.class)
	private Map<String, ExternalCallIteration> iterations = new LinkedHashMap<String, ExternalCallIteration>();

	public ExternalCall(String name, String displayableName, Integer lineNumber, CallType callType, 
			Integer iterationOrder, String iterationName, List<Parameter> parameters) {
		this.name = name;
		this.displayableName = displayableName;
		this.lineNumber = lineNumber;
		this.callType = callType;
		ExternalCallIteration callIteration = new ExternalCallIteration(iterationOrder, iterationName, parameters, Boolean.TRUE);
		iterations.put(iterationName, callIteration);
	}

	public ExternalCall(String name, Integer lineNumber, CallType callType, List<Parameter> parameters) {
		this(name, name, lineNumber, callType, 0, "iteration_0", parameters);
	}
	
	public ExternalCall() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayableName() {
		return displayableName;
	}

	public void setDisplayableName(String displayableName) {
		this.displayableName = displayableName;
	}

	public Integer getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	public CallType getCallType() {
		return callType;
	}

	public void setCallType(CallType callType) {
		this.callType = callType;
	}

	public Map<String, ExternalCallIteration> getIterations() {
		return iterations;
	}

	public void setIterations(Map<String, ExternalCallIteration> iterations) {
		this.iterations = iterations;
	}
	
	@JsonIgnore
	public ExternalCallIteration getFirstIteration() {
		if (iterations.isEmpty()) {
			return null;
		}
		return iterations.entrySet().iterator().next().getValue();
	}
	
	public String addIteration() {
		return addIteration(getFirstIteration().getParameters());
	}
	
	public String addIteration(List<Parameter> parameters) {
		int i = iterations.size();
		String iterationName = "iteration_" + i;
		addIteration(i, iterationName, parameters);
		return iterationName;
	}
	
	public void addIteration(Integer iterationOrder, String iterationName, List<Parameter> parameters) {
		ExternalCallIteration externalCallIteration = new ExternalCallIteration(iterationOrder, iterationName, parameters);
		iterations.put(iterationName, externalCallIteration);
		
	}
	
	public void setDefaultIteration(String iterationName) {
		throw new java.lang.UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String toString() {
		String parameters = "";
		if (!iterations.isEmpty()) {
			parameters = String.join(" ", getFirstIteration().getParameters().stream().map(Parameter::getGuiName).collect(Collectors.toList()));
		}
		return "CALL " + name + " USING " + parameters;
	}

	
	private static class IterationsSerializer extends JsonSerializer<Map<String, ExternalCallIteration>> {

		@Override
		public void serialize(Map<String, ExternalCallIteration> value, JsonGenerator gen,
				SerializerProvider serializers) throws IOException {
			gen.writeStartArray();
			for (ExternalCallIteration iteration: value.values()) {
				gen.writeObject(iteration);
			}
			gen.writeEndArray();
			
		}
	}
	
	private static class IterationDeserializer extends JsonDeserializer<Map<String, ExternalCallIteration>> {
		
		private static ObjectMapper mapper = new ObjectMapper();

		@Override
		public Map<String, ExternalCallIteration> deserialize(JsonParser p, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			Map<String, ExternalCallIteration> iterations = new LinkedHashMap<String, ExternalCallIteration>();
			ObjectCodec objectCodec = p.getCodec();
			JsonNode node = objectCodec.readTree(p);
			ObjectReader objectReader = mapper.readerFor(new TypeReference<List<ExternalCallIteration>>() {});
			List<ExternalCallIteration> callIterations = objectReader.readValue(node);
			for (ExternalCallIteration callIteration: callIterations) {
				iterations.put(callIteration.getName(), callIteration);
			}
			return iterations;
		}
		
	}
}
