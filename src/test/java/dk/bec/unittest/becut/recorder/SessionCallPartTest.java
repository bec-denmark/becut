package dk.bec.unittest.becut.recorder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import dk.bec.unittest.becut.recorder.model.SessionCallPart;

public class SessionCallPartTest {
	@Test
	public void test1() throws Exception {
		String log =
				"      * 02 RDZDB2:>EMPNO  of 01 RDZDB2:>EMPRECORD  = '      '           \r\n" + 
				"      * 03 RDZDB2:>FIRSTNME-LEN  of 02 RDZDB2:>FIRSTNME  of 01          \r\n" + 
				"      * RDZDB2:>EMPRECORD  = +00000                                     \r\n"; 
		List<String> in = DebugToolLogParser.splitLines(log);		
		List<String> sanitized = SessionCallPart.sanitize(in);
		List<String> expected = DebugToolLogParser.splitLines(
				"02 RDZDB2:>EMPNO  of 01 RDZDB2:>EMPRECORD  = '      '           \n" + 
				"03 RDZDB2:>FIRSTNME-LEN  of 02 RDZDB2:>FIRSTNME  of 01          RDZDB2:>EMPRECORD  = +00000                                     ");
		assertEquals(sanitized, expected);
	}

	@Test
	public void test11() throws Exception {
		String log =
				"02 RDZDB2:>EMPNO  of 01 RDZDB2:>EMPRECORD  = '      '           \r\n" + 
				"03 RDZDB2:>FIRSTNME-LEN  of 02 RDZDB2:>FIRSTNME  of 01          \r\n" + 
				"RDZDB2:>EMPRECORD  = +00000                                     \r\n"; 
		List<String> in = DebugToolLogParser.splitLines(log);		
		List<String> sanitized = SessionCallPart.sanitize(in);
		List<String> expected = DebugToolLogParser.splitLines(
				"02 RDZDB2:>EMPNO  of 01 RDZDB2:>EMPRECORD  = '      '           \n" + 
				"03 RDZDB2:>FIRSTNME-LEN  of 02 RDZDB2:>FIRSTNME  of 01          RDZDB2:>EMPRECORD  = +00000                                     ");
		assertEquals(sanitized, expected);
	}
	
	@Test
	public void test2() throws Exception {
		//sometimes debug tool 'forgets' to put '=' in record list, for now the 'fix' is to make debug tool log dataset 'wider'
		String log =
				"      * 03 RDZDB2:>SQLERRML  of 02 RDZDB2:>SQLERRM  of 01 RDZDB2:>SQLCA \r\n" + 
				"    * +00000\r\n"; 
		
		List<String> lines = DebugToolLogParser.splitLines(log);		
		SessionCallPart scp = new SessionCallPart(lines);
		assertNotNull(scp.getSessionRecord(3, "SQLERRML"));
		assertEquals(scp.getSessionRecord(3, "SQLERRML").getValue(), " +00000");
	}
	
	@Test
	public void test3() throws Exception {
		String log =
				"      * FD CALLER:>NUMBER-LIST  = OPEN INPUT, FILE STATUS: 00           \r\n" + 
				"      * 01 CALLER:>NUMBERS-RECORD  =                                    \r\n" + 
				"      * '                ADAM00000001                                   \r\n" + 
				"      *              '                                                  \r\n" + 
				"      * 02 CALLER:>WS-DESCRIPTION  of 01 CALLER:>WS-NUMBERS-FILE  =     \r\n" + 
				"      * '                ADAM'                                          \r\n" + 
				"      * 02 CALLER:>WS-NUM  of 01 CALLER:>WS-NUMBERS-FILE  = 00000001    \r\n" + 
				"      * 02 CALLER:>FILLER  of 01 CALLER:>WS-NUMBERS-FILE  =             \r\n" + 
				"      * '                                                               \r\n" + 
				"      *      '                                                          \r\n" + 
				"      * 01 CALLER:>NUMBER-LIST-EOF  = 'N'                               \r\n" + 
				"      * 01 CALLER:>CALLEE-PGM-NAME  = 'CALLEE  '                        \r\n" + 
				"      * 02 CALLER:>FILLER  of 01 CALLER:>CALLEE-AREA  = 'CALLEE  '      \r\n" + 
				"      * 02 CALLER:>CALLEE-DATA-LENGTH  of 01 CALLER:>CALLEE-AREA  =     \r\n" + 
				"      * +0000000000                                                     \r\n" + 
				"      * 03 CALLER:>CALLEE-RETURNCODE  of 02 CALLER:>CALLEE-DATA  of 01  \r\n" + 
				"      * CALLER:>CALLEE-AREA  =                                          \r\n" + 
				"      * 03 CALLER:>CALLEE-I-NUMBER  of 02 CALLER:>CALLEE-DATA  of 01    \r\n" + 
				"      * CALLER:>CALLEE-AREA  = 0000000001                               \r\n" + 
				"      * 03 CALLER:>CALLEE-RESULT  of 02 CALLER:>CALLEE-DATA  of 01      \r\n" + 
				"      * CALLER:>CALLEE-AREA  = 00000                                    \r\n"; 
		List<String> lines = DebugToolLogParser.splitLines(log);
		SessionCallPart scp = new SessionCallPart(lines);
		assertEquals("0000000001                               ", 
				scp.getSessionRecord(3, "CALLEE-I-NUMBER").getValue());
	}

	@Test
	public void test4() throws Exception {
		String log =
				"      * 01 MAT563:>NUMB  = 0000                                         \r\n" + 
				"      * 77 MAT563:>PGM-NAME  = 'MAT562  '                               \r\n"; 
		List<String> lines = DebugToolLogParser.splitLines(log);		
		SessionCallPart scp = new SessionCallPart(lines);
		assertEquals("0000                                         ", 
				scp.getSessionRecord(1, "NUMB").getValue());
		assertEquals("'MAT562  '                               ", 
				scp.getSessionRecord(77, "PGM-NAME").getValue());
	}

	@Test
	public void test40() throws Exception {
		String log =
				"      * 02 RDZDB2:>EMPNO  of 01 RDZDB2:>EMPRECORD  = '      '           \r\n" + 
				"      * 03 RDZDB2:>FIRSTNME-LEN  of 02 RDZDB2:>FIRSTNME  of 01          \r\n" + 
				"      * RDZDB2:>EMPRECORD  = +00000                                     \r\n" + 
				"      * 03 RDZDB2:>FIRSTNME-TEXT  of 02 RDZDB2:>FIRSTNME  of 01         \r\n" + 
				"      * RDZDB2:>EMPRECORD  = '            '                             \r\n" + 
				"      * 02 RDZDB2:>MIDINIT  of 01 RDZDB2:>EMPRECORD  = ' '              \r\n" + 
				"      * 03 RDZDB2:>LASTNAME-LEN  of 02 RDZDB2:>LASTNAME  of 01          \r\n" + 
				"      * RDZDB2:>EMPRECORD  = +00000                                     \r\n" + 
				"      * 03 RDZDB2:>LASTNAME-TEXT  of 02 RDZDB2:>LASTNAME  of 01         \r\n" + 
				"      * RDZDB2:>EMPRECORD  = '               '                          \r\n" + 
				"      * 02 RDZDB2:>WORKDEPT  of 01 RDZDB2:>EMPRECORD  = 'C01'           \r\n" + 
				"      * 02 RDZDB2:>PHONENO  of 01 RDZDB2:>EMPRECORD  = '    '           \r\n" + 
				"      * 02 RDZDB2:>HIREDATE  of 01 RDZDB2:>EMPRECORD  = '          '    \r\n" + 
				"      * 02 RDZDB2:>JOB  of 01 RDZDB2:>EMPRECORD  = '        '           \r\n" + 
				"      * 02 RDZDB2:>EDLEVEL  of 01 RDZDB2:>EMPRECORD  = +00000           \r\n" + 
				"      * 02 RDZDB2:>SEX  of 01 RDZDB2:>EMPRECORD  = ' '                  \r\n" + 
				"      * 02 RDZDB2:>BIRTHDATE  of 01 RDZDB2:>EMPRECORD  = '          '   \r\n" + 
				"      * 02 RDZDB2:>SALARY  of 01 RDZDB2:>EMPRECORD  =                   \r\n" + 
				"      * 02 RDZDB2:>BONUS  of 01 RDZDB2:>EMPRECORD  =                    \r\n" + 
				"      * 02 RDZDB2:>COMM  of 01 RDZDB2:>EMPRECORD  =                     \r\n"; 
		List<String> lines = DebugToolLogParser.splitLines(log);
		SessionCallPart scp = new SessionCallPart(lines);
		assertNotNull(scp.getSessionRecord(2, "WORKDEPT"));
		assertEquals(scp.getSessionRecord(2, "WORKDEPT").getValue(), "'C01'           ");
	}

	@Test
	public void test50() throws Exception {
		String log =
				"      * 02 RDZDB2:>SALARY  of 01 RDZDB2:>EMPRECORD  =                   \r\n"; 
		List<String> lines = DebugToolLogParser.splitLines(log);
		SessionCallPart scp = new SessionCallPart(lines);
		assertNotNull(scp.getSessionRecord(2, "SALARY"));
		assertEquals("                  ", scp.getSessionRecord(2, "SALARY").getValue());
	}
	
	@Test
	public void test60() throws Exception {
		String line = "03 RDZDB2:>SQLERRML  of 02 RDZDB2:>SQLERRM  of 01 RDZDB2:>SQLCA  = +00000"; 
		String[] parts = SessionCallPart.split(line);
		assertTrue(parts.length == 2);
	}

	@Test
	public void test61() throws Exception {
		String line = "03 RDZDB2:>SQLERRML  of 02 RDZDB2:>SQLERRM  of 01 RDZDB2:>SQLCA +00000"; 
		String[] parts = SessionCallPart.split(line);
		assertTrue(parts.length == 2);
		assertEquals("03 RDZDB2:>SQLERRML  of 02 RDZDB2:>SQLERRM  of 01 RDZDB2:>SQLCA", parts[0]);
		assertEquals(" +00000", parts[1]);
	}
	
	//TODO tables should be parsed
	@Test
	public void testSkipTable() throws Exception {
		String log = 
				"      * 02 RDZDB2:>SQLERRP  of 01 RDZDB2:>SQLCA  = '        '           \r\n" + 
				"      * SUB(1) of 02 RDZDB2:>SQLERRD  of 01 RDZDB2:>SQLCA  = +0000000000\r\n" + 
				"      * SUB(2) of 02 RDZDB2:>SQLERRD  of 01 RDZDB2:>SQLCA  = +0000000000\r\n" + 
				"      * SUB(3) of 02 RDZDB2:>SQLERRD  of 01 RDZDB2:>SQLCA  = +0000000000\r\n" + 
				"      * SUB(4) of 02 RDZDB2:>SQLERRD  of 01 RDZDB2:>SQLCA  = +0000000000\r\n" + 
				"      * SUB(5) of 02 RDZDB2:>SQLERRD  of 01 RDZDB2:>SQLCA  = +0000000000\r\n" + 
				"      * SUB(6) of 02 RDZDB2:>SQLERRD  of 01 RDZDB2:>SQLCA  = +0000000000\r\n"; 
		
		List<String> lines = DebugToolLogParser.splitLines(log);		
		SessionCallPart scp = new SessionCallPart(lines);
		assertEquals(2, scp.getRecords().size());
		assertEquals("'        '           ", 
				scp.getSessionRecord(2, "SQLERRP").getValue());
	}
}
