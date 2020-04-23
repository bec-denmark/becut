package dk.bec.unittest.becut.recorder;

import java.util.List;

public class RecorderJCLGenerator {
	private String programName;
	private String debugScript;
	private String jobName;
	private String userName;
	private List<String> steplib;

	public RecorderJCLGenerator(String programName, String debugScript, String jobName, String userName,
			List<String> steplib) {
		this.programName = programName.toUpperCase();
		this.debugScript = debugScript;
		this.jobName = jobName.toUpperCase();
		this.userName = userName.toUpperCase();
		this.steplib = steplib;
	}

	public String getJCL() {
		String jcl = "//" +  jobName + " JOB ,'" + userName + "',\n";
		jcl += "//             SCHENV=TSTSYS,\n";
		jcl += "//             MSGCLASS=Q,\n";
		jcl += "//             NOTIFY=" + userName +",\n";
		jcl += "//             UJOBCORR=" + userName +"\n";
		jcl += "//PGMEXEC  EXEC PGM=" + programName + "\n";
		jcl += generateSteplib();
		jcl += "//INSPIN      DD *\n";
		jcl += debugScript;
		jcl += "\n";
		jcl += "/*\n";
		jcl += "//INSPLOG   DD SYSOUT=*\n";
		jcl += "//INSPCMD   DD DSN=SYS2.DEBUG.COMMANDS,DISP=SHR\n";
		jcl += "//CEEOPTS   DD *,DLM='/*'\n";
		jcl += "TEST(,INSPIN,,)\n";
		jcl += "/*\n";
		
		return jcl;
	}
	
	private String generateSteplib() {
		String s = "//STEPLIB   DD DSN=" + steplib.get(0).toUpperCase() + ",DISP=SHR\n";
		if (steplib.size() > 1) {
			for (int i = 1; i < steplib.size(); i++) {
			s += "//          DD DSN=" + steplib.get(i).toUpperCase() + ",DISP=SHR\n";
			}
		}
		return s;
	}
}
