package dk.bec.unittest.becut.integrationtests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import junit.framework.TestCase;

public class TestCaseDeserializationIT extends TestCase {

	private static ObjectMapper mapper = new ObjectMapper();
	
	@Test
	public void testDeserializeMAT510RS() {
		File file = new File("./src/test/resources/testcases/mat510rs_testcase.json");
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(file);
			BecutTestCase becutTestCase = mapper.readValue(fileInputStream, BecutTestCase.class);
			//TODO add assertions here
			System.out.println("We're done");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
