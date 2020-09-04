package dk.bec.unittest.becut.recorder;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import dk.bec.unittest.becut.recorder.model.SessionCallPart;

public class SessionCallPartTest {
	@Test
	public void test0() throws Exception {
		String log =
				"      * FD CALLER:>NUMBER-LIST  = OPEN INPUT, FILE STATUS: 00           \n" + 
				"      * 01 CALLER:>NUMBERS-RECORD  =                                    " + 
				"'                ADAM00000001                                   " + 
				"              '                                                  \n" + 
				"      * 02 CALLER:>WS-DESCRIPTION  of 01 CALLER:>WS-NUMBERS-FILE  =     " + 
				"'                ADAM'                                          \n" + 
				"      * 02 CALLER:>WS-NUM  of 01 CALLER:>WS-NUMBERS-FILE  = 00000001    \n" + 
				"      * 02 CALLER:>FILLER  of 01 CALLER:>WS-NUMBERS-FILE  =             " + 
				"'                                                               " + 
				"     '                                                          \n" + 
				"      * 01 CALLER:>NUMBER-LIST-EOF  = 'N'                               \n" + 
				"      * 01 CALLER:>CALLEE-PGM-NAME  = 'CALLEE  '                        \n" + 
				"      * 02 CALLER:>FILLER  of 01 CALLER:>CALLEE-AREA  = 'CALLEE  '      \n" + 
				"      * 02 CALLER:>CALLEE-DATA-LENGTH  of 01 CALLER:>CALLEE-AREA  =     " + 
				"+0000000000                                                     \n" + 
				"      * 03 CALLER:>CALLEE-RETURNCODE  of 02 CALLER:>CALLEE-DATA  of 01  " + 
				"CALLER:>CALLEE-AREA  = ";
		List<String> lines = sanitize(DebugToolLogParser.splitLines(log));
		SessionCallPart scp = new SessionCallPart(lines);
		System.out.println(scp.getRecords());
	}
	
	@Test
	public void test1() throws Exception {
		String log =
				"      * FD CALLER:>NUMBER-LIST  = OPEN INPUT, FILE STATUS: 00           \n" + 
				"      * 01 CALLER:>NUMBERS-RECORD  =                                    \n" + 
				"      * '                ADAM00000001                                   \n" + 
				"      *              '                                                  \n" + 
				"      * 02 CALLER:>WS-DESCRIPTION  of 01 CALLER:>WS-NUMBERS-FILE  =     \n" + 
				"      * '                ADAM'                                          \n" + 
				"      * 02 CALLER:>WS-NUM  of 01 CALLER:>WS-NUMBERS-FILE  = 00000001    \n" + 
				"      * 02 CALLER:>FILLER  of 01 CALLER:>WS-NUMBERS-FILE  =             \n" + 
				"      * '                                                               \n" + 
				"      *      '                                                          \n" + 
				"      * 01 CALLER:>NUMBER-LIST-EOF  = 'N'                               \n" + 
				"      * 01 CALLER:>CALLEE-PGM-NAME  = 'CALLEE  '                        \n" + 
				"      * 02 CALLER:>FILLER  of 01 CALLER:>CALLEE-AREA  = 'CALLEE  '      \n" + 
				"      * 02 CALLER:>CALLEE-DATA-LENGTH  of 01 CALLER:>CALLEE-AREA  =     \n" + 
				"      * +0000000000                                                     \n" + 
				"      * 03 CALLER:>CALLEE-RETURNCODE  of 02 CALLER:>CALLEE-DATA  of 01  \n" + 
				"      * CALLER:>CALLEE-AREA  = ";
		
		List<String> lines = sanitize(DebugToolLogParser.splitLines(log));
		
		SessionCallPart scp = new SessionCallPart(lines);
		System.out.println(scp.getRecords());
	}

	@Test
	public void test2() throws Exception {
		String log =
				"    * AT EXIT BEGIN                                                   \r\n" + 
				"    * %PROGRAM = SUMA                                                 \r\n" + 
				"    * At EXIT in COBOL program SUMA ::> SUMA.                         \r\n" + 
				"    * FD SUMA:>NUM-LIST  = CLOSED, FILE STATUS: 00                    \r\n" + 
				"    * The address of 01 SUMA:>NUM-LIST-FIELDS   has been determined to\r\n" + 
				"    * invalid.                                                        \r\n" + 
				"    * FD SUMA:>NUM-LIST-2  = OPEN INPUT, FILE STATUS: 10              \r\n" + 
				"    * 02 SUMA:>NUM-VALUE-1  of 01 SUMA:>NUM-LIST-FIELDS-2  = 04       \r\n" + 
				"    * 02 SUMA:>NUM-VALUE-2  of 01 SUMA:>NUM-LIST-FIELDS-2  = 004      \r\n" + 
				"    * 02 SUMA:>WS-NUM  of 01 SUMA:>WS-LIST-FIELDS  = \r\n"; 
		List<String> lines = sanitize(DebugToolLogParser.splitLines(log));		
		SessionCallPart scp = new SessionCallPart(lines);
		System.out.println(scp.getRecords());
	}

	@Test
	public void test3() throws Exception {
		String log =
				"      * AT EXIT BEGIN\r\n" + 
				"      * %PROGRAM = RDZDB2\r\n" + 
				"      * At LINE 271.1 in COBOL program RDZDB2 ::> RDZDB2.\r\n" + 
				"      * 01 RDZDB2:>EMPRECORD\r\n" + 
				"      *    02 RDZDB2:>EMPNO    '000030'\r\n" + 
				"      *    02 RDZDB2:>FIRSTNME\r\n" + 
				"      *       03 RDZDB2:>FIRSTNME-LEN    +00005\r\n" + 
				"      *       03 RDZDB2:>FIRSTNME-TEXT    'SALLY"; 
		
		List<String> lines = sanitize(DebugToolLogParser.splitLines(log));		
		SessionCallPart scp = new SessionCallPart(lines);
		System.out.println(scp.getRecords());
	}
	
	List<String> sanitize(List<String> list) {
		LinkedList<String> lines = new LinkedList<>();
		list.forEach(line -> {
			line = DebugToolLogParser.removeProlog(line);
			if(!lines.isEmpty()) {
				if(lines.peekLast().matches("\\d+ .*") && !line.matches("\\d+ .*")) {
					lines.add(lines.pollLast() + line);
				} else {
					lines.add(line);
				}
			} else {
				lines.add(line);
			}
		});
		return lines;
	}
}
