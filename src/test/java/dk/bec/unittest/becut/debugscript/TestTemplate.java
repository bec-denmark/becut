package dk.bec.unittest.becut.debugscript;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TestTemplate {
	@Test
	public void testTemplate() {
		List<String> lines = Arrays.asList(
				"//${jobname} JOB ,'${user}',", 
				"//             SCHENV=TSTSYS,", 
				"//             MSGCLASS=Q,", 
				"//             NOTIFY=${uSeR},", 
				"//             UJOBCORR=${user}",
				"..",
				"//STEPLIB   ..", 
				"${dd}",
				"//INSPIN      DD *", 
				"${debug}", 
				"/*"
		);
		
		assertEquals("//BECUT JOB ,'TST1',\n" + 
				"//             SCHENV=TSTSYS,\n" + 
				"//             MSGCLASS=Q,\n" + 
				"//             NOTIFY=TST1,\n" + 
				"//             UJOBCORR=TST1\n" + 
				"..\n" + 
				"//STEPLIB   ..\n" + 
				"FXSORTED    DD DSN=TST1.BECUT.T644111,DISP=SHR\n" + 
				"\n" + 
				"//INSPIN      DD *\n" + 
				"GO;\n" + 
				"/*", JCLTemplate.fillTemplate(lines, "TST1", "SUMA", "BECUT", 
				"FXSORTED    DD DSN=TST1.BECUT.T644111,DISP=SHR\n", "GO;", "")
				);
		
	}
	
	@Test
	public void shouldNotGenerateEMptyLines() {
		List<String> lines = Arrays.asList(
				"//${jobname} JOB ,'${user}',", 
				"//             UJOBCORR=${user}",
				"${dd}",
				"${debug}", 
				"/*"
		);
		//DISP=SHR,DSN=SYS1.EQAW.SEQAMOD
		System.out.println(JCLTemplate.fillTemplate(lines, "TST1", "SUMA", "BECUT", "", "GO;", ""));
		
		assertEquals(
				"//BECUT JOB ,'TST1',\n" + 
				"//             UJOBCORR=TST1\n" + 
				"//*\n" + 
				"GO;\n" + 
				"/*", 
				JCLTemplate.fillTemplate(lines, "TST1", "SUMA", "BECUT", "", "GO;", "")
				);
		
	}
}
