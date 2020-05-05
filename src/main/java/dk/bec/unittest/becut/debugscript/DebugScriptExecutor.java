package dk.bec.unittest.becut.debugscript;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;

import dk.bec.unittest.becut.Settings;
import dk.bec.unittest.becut.debugscript.model.DebugScript;
import dk.bec.unittest.becut.ftp.FTPManager;
import dk.bec.unittest.becut.ftp.model.HostJob;
import dk.bec.unittest.becut.ui.model.BECutAppContext;

public class DebugScriptExecutor {
	
	public static HostJob testBatch(String jobName, String programName, DebugScript debugScript) {
		FTPClient ftpClient = new FTPClient();
		try {
			FTPManager.connectAndLogin(ftpClient, BECutAppContext.getContext().getCredential());
			InputStream is = new ByteArrayInputStream(createJCL(jobName, programName, debugScript.generate()).getBytes());
			return FTPManager.submitJobAndWaitToComplete(ftpClient, is, 60, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static String createJCL(String jobName, String programName, String debugScript) {
		String userName = Settings.USERNAME;
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
	private static String generateSteplib() {
		List<String> steplib = Settings.STEPLIB;
		String s = "//STEPLIB   DD DSN=" + steplib.get(0).toUpperCase() + ",DISP=SHR\n";
		if (steplib.size() > 1) {
			for (int i = 1; i < steplib.size(); i++) {
			s += "//          DD DSN=" + steplib.get(i).toUpperCase() + ",DISP=SHR\n";
			}
		}
		return s;
	}
}
