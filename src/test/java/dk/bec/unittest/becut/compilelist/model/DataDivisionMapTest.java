package dk.bec.unittest.becut.compilelist.model;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import dk.bec.unittest.becut.compilelist.Parse;
import junit.framework.TestCase;

public class DataDivisionMapTest extends TestCase {

	public void testSourceMAT510RS() {
		File file = new File("./src/test/resources/compilelistings/mat510rs_compile_listing.txt");
		try {
			CompileListing compileListing = Parse.parse(file);
			DataDivisionMap dataDivisionMap = compileListing.getDataDivisionMap();
			assertThat(dataDivisionMap.getRecords().keySet(), hasSize(3));
			
			assertThat(dataDivisionMap.getRecords().get(16), hasProperty("lineNumber", equalTo(16)));
			assertThat(dataDivisionMap.getRecords().get(16), hasProperty("level", equalTo(1)));
			assertThat(dataDivisionMap.getRecords().get(16), hasProperty("name", equalTo("MAT511-PGM-NAVN")));
			assertThat(dataDivisionMap.getRecords().get(16), hasProperty("dataType", equalTo(DataType.PIC)));
			assertThat(dataDivisionMap.getRecords().get(16), hasProperty("size", equalTo(8)));
			assertThat(dataDivisionMap.getRecords().get(16), hasProperty("subRecords", aMapWithSize(0)));
			assertThat(dataDivisionMap.getRecords().get(16), hasProperty("parent", nullValue()));
			assertThat(dataDivisionMap.getRecords().get(16), hasProperty("isSeventySeven", is(Boolean.FALSE)));

			assertThat(dataDivisionMap.getRecords().get(18), hasProperty("lineNumber", equalTo(18)));
			assertThat(dataDivisionMap.getRecords().get(18), hasProperty("level", equalTo(1)));
			assertThat(dataDivisionMap.getRecords().get(18), hasProperty("name", equalTo("MAT511-AREA")));
			assertThat(dataDivisionMap.getRecords().get(18), hasProperty("dataType", equalTo(DataType.GROUP)));
			assertThat(dataDivisionMap.getRecords().get(18), hasProperty("size", equalTo(30)));
			assertThat(dataDivisionMap.getRecords().get(18), hasProperty("subRecords", aMapWithSize(3)));
			assertThat(dataDivisionMap.getRecords().get(18), hasProperty("parent", nullValue()));
			assertThat(dataDivisionMap.getRecords().get(18), hasProperty("isSeventySeven", is(Boolean.FALSE)));

			assertThat(dataDivisionMap.getRecord(22), hasProperty("lineNumber", equalTo(22)));
			assertThat(dataDivisionMap.getRecord(22), hasProperty("level", equalTo(2)));
			assertThat(dataDivisionMap.getRecord(22), hasProperty("name", equalTo("MAT511-DATA")));
			assertThat(dataDivisionMap.getRecord(22), hasProperty("dataType", equalTo(DataType.GROUP)));
			assertThat(dataDivisionMap.getRecord(22), hasProperty("size", equalTo(18)));
			assertThat(dataDivisionMap.getRecord(22), hasProperty("subRecords", aMapWithSize(3)));
			assertThat(dataDivisionMap.getRecord(22), hasProperty("parent", hasProperty("name", is("MAT511-AREA"))));
			assertThat(dataDivisionMap.getRecord(22), hasProperty("isSeventySeven", is(Boolean.FALSE)));

			assertThat(dataDivisionMap.getRecord("MAT511-DATA").get(0), hasProperty("lineNumber", equalTo(22)));
			assertThat(dataDivisionMap.getRecord("MAT511-DATA").get(0), hasProperty("level", equalTo(2)));
			assertThat(dataDivisionMap.getRecord("MAT511-DATA").get(0), hasProperty("name", equalTo("MAT511-DATA")));
			assertThat(dataDivisionMap.getRecord("MAT511-DATA").get(0), hasProperty("dataType", equalTo(DataType.GROUP)));
			assertThat(dataDivisionMap.getRecord("MAT511-DATA").get(0), hasProperty("size", equalTo(18)));
			assertThat(dataDivisionMap.getRecord("MAT511-DATA").get(0), hasProperty("subRecords", aMapWithSize(3)));
			assertThat(dataDivisionMap.getRecord("MAT511-DATA").get(0), hasProperty("parent", hasProperty("name", is("MAT511-AREA"))));
			assertThat(dataDivisionMap.getRecord("MAT511-DATA").get(0), hasProperty("isSeventySeven", is(Boolean.FALSE)));

			assertThat(dataDivisionMap.getRecord(24), hasProperty("lineNumber", equalTo(24)));
			assertThat(dataDivisionMap.getRecord(24), hasProperty("level", equalTo(88)));
			assertThat(dataDivisionMap.getRecord(24), hasProperty("name", equalTo("MAT511-RETURKODE-OK")));
			assertThat(dataDivisionMap.getRecord(24), hasProperty("dataType", equalTo(DataType.EIGHTYEIGHT)));
			assertThat(dataDivisionMap.getRecord(24), hasProperty("size", equalTo(4)));
			assertThat(dataDivisionMap.getRecord(24), hasProperty("subRecords", aMapWithSize(0)));
			assertThat(dataDivisionMap.getRecord(24), hasProperty("parent", hasProperty("name", is("MAT511-RETURKODE"))));
			assertThat(dataDivisionMap.getRecord(24), hasProperty("isSeventySeven", is(Boolean.FALSE)));

			assertThat(dataDivisionMap.getRecord("MAT511-RETURKODE-OK").get(0), hasProperty("lineNumber", equalTo(24)));
			assertThat(dataDivisionMap.getRecord("MAT511-RETURKODE-OK").get(0), hasProperty("level", equalTo(88)));
			assertThat(dataDivisionMap.getRecord("MAT511-RETURKODE-OK").get(0), hasProperty("name", equalTo("MAT511-RETURKODE-OK")));
			assertThat(dataDivisionMap.getRecord("MAT511-RETURKODE-OK").get(0), hasProperty("dataType", equalTo(DataType.EIGHTYEIGHT)));
			assertThat(dataDivisionMap.getRecord("MAT511-RETURKODE-OK").get(0), hasProperty("size", equalTo(4)));
			assertThat(dataDivisionMap.getRecord("MAT511-RETURKODE-OK").get(0), hasProperty("subRecords", aMapWithSize(0)));
			assertThat(dataDivisionMap.getRecord("MAT511-RETURKODE-OK").get(0), hasProperty("parent", hasProperty("name", is("MAT511-RETURKODE"))));
			assertThat(dataDivisionMap.getRecord("MAT511-RETURKODE-OK").get(0), hasProperty("isSeventySeven", is(Boolean.FALSE)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}

	public void testSourceMAT512RS() {
		File file = new File("./src/test/resources/compilelistings/mat512rs_compile_listing.txt");
		try {
			CompileListing compileListing = Parse.parse(file);
			DataDivisionMap dataDivisionMap = compileListing.getDataDivisionMap();
			assertThat(dataDivisionMap.getRecords().keySet(), hasSize(18));
			
			assertThat(dataDivisionMap.getRecords().get(7), hasProperty("lineNumber", equalTo(7)));
			assertThat(dataDivisionMap.getRecords().get(7), hasProperty("level", equalTo(1)));
			assertThat(dataDivisionMap.getRecords().get(7), hasProperty("name", equalTo("SQLCA")));
			assertThat(dataDivisionMap.getRecords().get(7), hasProperty("dataType", equalTo(DataType.GROUP)));
			assertThat(dataDivisionMap.getRecords().get(7), hasProperty("size", equalTo(136)));
			assertThat(dataDivisionMap.getRecords().get(7), hasProperty("subRecords", aMapWithSize(8)));
			assertThat(dataDivisionMap.getRecords().get(7), hasProperty("parent", nullValue()));
			assertThat(dataDivisionMap.getRecords().get(7), hasProperty("isSeventySeven", is(Boolean.FALSE)));

			assertThat(dataDivisionMap.getRecord(138), hasProperty("lineNumber", equalTo(138)));
			assertThat(dataDivisionMap.getRecord(138), hasProperty("level", equalTo(88)));
			assertThat(dataDivisionMap.getRecord(138), hasProperty("name", equalTo("SQL-INIT-DONE")));
			assertThat(dataDivisionMap.getRecord(138), hasProperty("dataType", equalTo(DataType.EIGHTYEIGHT)));
			assertThat(dataDivisionMap.getRecord(138), hasProperty("size", equalTo(2)));
			assertThat(dataDivisionMap.getRecord(138), hasProperty("subRecords", aMapWithSize(0)));
			assertThat(dataDivisionMap.getRecord(138), hasProperty("parent", hasProperty("name", is("SQL-INIT-FLAG"))));
			assertThat(dataDivisionMap.getRecord(138), hasProperty("isSeventySeven", is(Boolean.FALSE)));

			assertThat(dataDivisionMap.getRecord(132), hasProperty("lineNumber", equalTo(132)));
			assertThat(dataDivisionMap.getRecord(132), hasProperty("level", equalTo(77)));
			assertThat(dataDivisionMap.getRecord(132), hasProperty("name", equalTo("DSN-TMP2")));
			assertThat(dataDivisionMap.getRecord(132), hasProperty("dataType", equalTo(DataType.PACKED_DECIMAL)));
			assertThat(dataDivisionMap.getRecord(132), hasProperty("size", equalTo(10)));
			assertThat(dataDivisionMap.getRecord(132), hasProperty("subRecords", aMapWithSize(0)));
			assertThat(dataDivisionMap.getRecord(132), hasProperty("parent", nullValue()));
			assertThat(dataDivisionMap.getRecord(132), hasProperty("isSeventySeven", is(Boolean.TRUE)));

			assertThat(dataDivisionMap.getRecord(168), hasProperty("lineNumber", equalTo(168)));
			assertThat(dataDivisionMap.getRecord(168), hasProperty("level", equalTo(5)));
			assertThat(dataDivisionMap.getRecord(168), hasProperty("name", equalTo("SQL-PVAR-ADDRS1")));
			assertThat(dataDivisionMap.getRecord(168), hasProperty("dataType", equalTo(DataType.GROUP)));
			assertThat(dataDivisionMap.getRecord(168), hasProperty("size", equalTo(8)));
			assertThat(dataDivisionMap.getRecord(168), hasProperty("subRecords", aMapWithSize(2)));
			assertThat(dataDivisionMap.getRecord(168), hasProperty("parent", hasProperty("name", is("SQLVAR-BASE1"))));
			assertThat(dataDivisionMap.getRecord(168), hasProperty("parent", hasProperty("subRecords", aMapWithSize(4))));
			assertThat(dataDivisionMap.getRecord(168), hasProperty("isSeventySeven", is(Boolean.FALSE)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}

	public void testSourceMAT514RS() {
		File file = new File("./src/test/resources/compilelistings/mat514rs_syntaxcheck_listing.txt");
		try {
			CompileListing compileListing = Parse.parse(file);
			DataDivisionMap dataDivisionMap = compileListing.getDataDivisionMap();
			assertThat(dataDivisionMap.getRecords().keySet(), hasSize(1));

			assertThat(dataDivisionMap.getRecord(9), hasProperty("lineNumber", equalTo(9)));
			assertThat(dataDivisionMap.getRecord(9), hasProperty("level", equalTo(2)));
			assertThat(dataDivisionMap.getRecord(9), hasProperty("name", equalTo("MAT514-RETURKODE")));
			assertThat(dataDivisionMap.getRecord(9), hasProperty("dataType", equalTo(DataType.PIC_NUMERIC)));
			assertThat(dataDivisionMap.getRecord(9), hasProperty("size", equalTo(4)));
			assertThat(dataDivisionMap.getRecord(9), hasProperty("subRecords", aMapWithSize(5)));
			assertThat(dataDivisionMap.getRecord(9), hasProperty("parent", hasProperty("name", is("MAT514-AREA"))));
			assertThat(dataDivisionMap.getRecord(9), hasProperty("isSeventySeven", is(Boolean.FALSE)));

			assertThat(dataDivisionMap.getRecord(13), hasProperty("lineNumber", equalTo(13)));
			assertThat(dataDivisionMap.getRecord(13), hasProperty("level", equalTo(88)));
			assertThat(dataDivisionMap.getRecord(13), hasProperty("name", equalTo("MAT514-RETURKODE-DB2-FEJL")));
			assertThat(dataDivisionMap.getRecord(13), hasProperty("dataType", equalTo(DataType.EIGHTYEIGHT)));
			assertThat(dataDivisionMap.getRecord(13), hasProperty("size", equalTo(4)));
			assertThat(dataDivisionMap.getRecord(13), hasProperty("subRecords", aMapWithSize(0)));
			assertThat(dataDivisionMap.getRecord(13), hasProperty("parent", hasProperty("name", is("MAT514-RETURKODE"))));
			assertThat(dataDivisionMap.getRecord(13), hasProperty("isSeventySeven", is(Boolean.FALSE)));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}

	public void testShouldParseLastRecordDescription() {
		String listingPart = "Data Definition Attribute codes (rightmost column) have the following meanings:                                                     \r\n" + 
				"    D = Object of OCCURS DEPENDING    G = GLOBAL                             S = Spanned file                                       \r\n" + 
				"    E = EXTERNAL                      O = Has OCCURS clause                  U = Undefined format file                              \r\n" + 
				"    F = Fixed-length file             OG= Group has own length definition    V = Variable-length file                               \r\n" + 
				"    FB= Fixed-length blocked file     R = REDEFINES                          VB= Variable-length blocked file                       \r\n" + 
				"Source   Hierarchy and                                    Base       Hex-Displacement  Asmblr Data                    Data Def      \r\n" + 
				"LineID   Data Name                                        Locator    Blk   Structure   Definition      Data Type      Attributes    \r\n" + 
				"     4  PROGRAM-ID SUMMER------------------------------------------------------------------------------------------------------*    \r\n" + 
				"    16   FD NUM-LIST. . . . . . . . . . . . . . . . . . .                                              QSAM           F             \r\n" + 
				"    17   1  NUM-LIST-FIELDS . . . . . . . . . . . . . . . BLF=00000  000               DS 0CL80        Group                        \r\n" + 
				"    18     2  NUM-VALUE . . . . . . . . . . . . . . . . . BLF=00000  000   0 000 000   DS 4C           Disp-Num                     \r\n" + 
				"    19     2  FILLER. . . . . . . . . . . . . . . . . . . BLF=00000  004   0 000 004   DS 76C          Display                      \r\n" + 
				"    22   1  WS-LIST-FIELDS. . . . . . . . . . . . . . . . BLW=00000  000               DS 0CL80        Group                        \r\n" + 
				"    23     2  WS-NUM. . . . . . . . . . . . . . . . . . . BLW=00000  000   0 000 000   DS 4C           Disp-Num                     \r\n" + 
				"    24     2  FILLER. . . . . . . . . . . . . . . . . . . BLW=00000  004   0 000 004   DS 76C          Display                      \r\n" + 
				"    25   1  WS-EOF. . . . . . . . . . . . . . . . . . . . BLW=00000  050               DS 1C           Alphabetic                   \r\n" + 
				"    26   1  WS-SUM. . . . . . . . . . . . . . . . . . . . BLW=00000  058               DS 5C           Disp-Num                     ";
		
		List<String> lines = Arrays.asList(listingPart.split("\\r\\n"));

		DataDivisionMap dd = new DataDivisionMap(lines);
		assertTrue(dd.getRecord("WS-SUM") != null);
	}
}
