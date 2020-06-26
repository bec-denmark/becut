package dk.bec.unittest.becut.recorder;

import java.util.List;

public class RecorderJCLGenerator {
	
	public static String getJCL(String programName, String datasetName, String jobName, String userName, List<String> steplib) {
		String jcl = "" +
				"//" +  jobName + " JOB ,'" + userName + "',\n" +
				"//             SCHENV=TSTSYS,\n" +
				"//             MSGCLASS=Q,\n" +
				"//             NOTIFY=" + userName +",\n" + 
				"//             UJOBCORR=" + userName +"\n" +
				"//PGMEXEC  EXEC PGM=" + programName + "\n" +
				generateSteplib(steplib) +
				"//INSPIN    DD *\n" + 
				"            SET LOG ON FILE " + datasetName + ";\n" + 
				"            SET DYNDEBUG OFF;\n" + 
				"            AT CALL * BEGIN;\n" + 
				"               LIST UNTITLED('AT CALL BEGIN');\n" + 
				"               LIST CALLS;\n" + 
				"               LIST TITLED *;\n" + 
				"               LIST UNTITLED('AT CALL END');\n" + 
				"               LIST UNTITLED('');\n" + 
				"               GO;\n" + 
				"            END;\n" + 
				"            AT EXIT * BEGIN;\n" + 
				"               LIST UNTITLED('AT EXIT BEGIN');\n" + 
				"               SET QUALIFY RETURN;\n" + 
				"               LIST CALLS;\n" + 
				"               LIST TITLED *;\n" + 
				"               LIST UNTITLED('AT EXIT END');\n" + 
				"               LIST UNTITLED('');\n" + 
				"               GO;\n" + 
				"            END;\n" + 
				"            GO;\n" + 
				"            QUIT;\n" + 
				"/*\n" + 
				"//INSPLOG   DD SYSOUT=*\n" + 
				"//INSPCMD   DD DSN=SYS2.DEBUG.COMMANDS,DISP=SHR\n" + 
				"//CEEOPTS   DD *,DLM='/*'\n" + 
				"TEST(,INSPIN,,)\n" + 
				"/*";
		
		return jcl.toUpperCase();
	}
	
	private static String generateSteplib(List<String> steplib) {
		String s = "//STEPLIB   DD DSN=" + steplib.get(0).toUpperCase() + ",DISP=SHR\n";
		if (steplib.size() > 1) {
			for (int i = 1; i < steplib.size(); i++) {
				s += "//          DD DSN=" + steplib.get(i).toUpperCase() + ",DISP=SHR\n";
			}
		}
		return s;
	}	
}
