package dk.bec.unittest.becut.debugscript.model;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import dk.bec.unittest.becut.compilelist.model.Record;
import dk.bec.unittest.becut.debugscript.model.statement.Move;
import dk.bec.unittest.becut.testcase.model.Parameter;
import junit.framework.TestCase;

public class MoveTest extends TestCase {
	
	String record1 = "     6   1  TMAT5110. . . . . . . . . . . . . . . . . . . BLW=00000  000               DS 8C           Display";
	String record2 = "    27       3  MAT511-I-NUMBER . . . . . . . . . . . . . BLW=00000  020   0 000 010   DS 4C           Disp-Num";
	String record3 = "    24       88 MAT511-RETURKODE-OK . . . . . . . . . . .";
	
	@Test
	public void testPic() {
		Record record = new Record(record1);
		Parameter parameter = new Parameter(record);
		parameter.setValue("TMAT5110");
		Move moveStatement = new Move(parameter);
		assertThat(moveStatement.generate(), equalTo("       MOVE 'TMAT5110' TO TMAT5110;"));
		
	}
	
	public void testPicNumeric() {
		Record record = createRecord(Stream.of(record1, record2).collect(Collectors.toList()));
		Parameter parameters = new Parameter(record);
		Parameter parameter = parameters.getSubStructure().get(0);
		parameter.setValue("1234");
		Move moveStatement = new Move(parameter);
		assertThat(moveStatement.generate(), equalTo("       MOVE 1234 TO MAT511-I-NUMBER;"));
		
	}
	
	public void test88() {
		Record record = createRecord(Stream.of(record1, record3).collect(Collectors.toList()));
		Parameter parameters = new Parameter(record);
		Parameter parameter = parameters.getSubStructure().get(0);
		parameter.setValue("TRUE");
		Move moveStatement = new Move(parameter);
		assertThat(moveStatement.generate(), equalTo("       SET MAT511-RETURKODE-OK TO TRUE;"));
		
	}
	
	private Record createRecord(List<String> recordLines) {
		List<Record> records = new ArrayList<Record>(recordLines.size());
		for (String line: recordLines) {
			records.add(new Record(line));
		}
		Record.groupToRecord(records);
		return records.get(0);
	}

}
