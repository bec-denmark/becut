package dk.bec.unittest.becut.compilelist.sql;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import dk.bec.unittest.becut.compilelist.Parse;
import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.compilelist.model.Record;
import junit.framework.TestCase;

public class SQLParseTest extends TestCase {
	
	private String fetchSQL = "EXEC SQL\r\n FETCH CSR-ABC12345\r\n INTO  :S01";
	private String fetchSQL2 = "FETCH ABC123 INTO :ABC1234 ,:ABC1234, :ABC1234,  :ABC1234";
	private String selectSQL = "EXEC SQL SELECT TABLE.FIELD1_I ,TABLE.FIELD2_I ,TABLE.FIELD3_N ,TABLE.FIELD4_N ,TABLE.FIELD5_N INTO  :TABLE-FIELD1-I ,:TABLE-FIELD2-I ,:TABLE-FIELD3-N ,:TABLE-FIELD4-N ,:TABLE-FIELD5-N FROM SOMETABLE TABLE WHERE TABLE.FIELD1_I >= :TABLE-FIELD1-I AND  TABLE.FIELD2_I >= :TABLE-FIELD2-I FETCH FIRST 1 ROWS ONLY END-EXEC";
	
	@Test
	public void testGetHostVariableNames() {
		
		List<String> fetchSQLNamesExpected = Arrays.asList("S01");
		List<String> fetchSQL2NamesExpected = Arrays.asList("ABC1234", "ABC1234", "ABC1234", "ABC1234");
		List<String> selectSQLNamesExpected = Arrays.asList("TABLE-FIELD1-I", "TABLE-FIELD2-I", "TABLE-FIELD3-N", "TABLE-FIELD4-N", "TABLE-FIELD5-N");
		
		List<String> fetchSQLNames = SQLParse.getHostVariableNames(fetchSQL);
		assertThat(fetchSQLNames, is(fetchSQLNamesExpected));
		
		List<String> fetchSQL2Names = SQLParse.getHostVariableNames(fetchSQL2);
		assertThat(fetchSQL2Names, is(fetchSQL2NamesExpected));

		List<String> selectSQLNames = SQLParse.getHostVariableNames(selectSQL);
		assertThat(selectSQLNames, is(selectSQLNamesExpected));
	}
	
	@Test
	public void testGetHostVariables() {
		String sql = "EXEC SQL                     \n" + 
					 "   SELECT EMP.FIRSTNME       \n" + 
					 "         ,EMP.MIDINIT        \n" + 
					 "         ,EMP.LASTNAME       \n" + 
					 "   INTO  :FIRSTNME           \n" + 
					 "        ,:MIDINIT            \n" + 
					 "        ,:LASTNAME           \n" + 
					 "   FROM   DSN8710.EMP EMP    \n" + 
					 "   WHERE EMP.EMPNO = :EMPNO  \n" + 
					 "   FETCH FIRST ROW ONLY      \n" + 
					 "END-EXEC                     ";

		File file = new File("./src/test/resources/compilelistings/mat512rs_compile_listing.txt");
		try {
			CompileListing compileListing = Parse.parse(file);
			List<Record> records = SQLParse.getHostVariables(sql, compileListing);
			assertThat(records.size(), is(3));
			assertThat(records.get(0), hasProperty("name", equalTo("FIRSTNME")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

}
