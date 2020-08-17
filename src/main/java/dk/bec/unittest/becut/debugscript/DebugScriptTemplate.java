package dk.bec.unittest.becut.debugscript;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import dk.bec.unittest.becut.Settings;

public class DebugScriptTemplate {
	static final Pattern $USER = Pattern.compile(Pattern.quote("${user}"), Pattern.CASE_INSENSITIVE);
	static final Pattern $PROGRAM = Pattern.compile(Pattern.quote("${program}"), Pattern.CASE_INSENSITIVE);
	static final Pattern $JOBNAME = Pattern.compile(Pattern.quote("${jobname}"), Pattern.CASE_INSENSITIVE);
	static final Pattern $DD = Pattern.compile(Pattern.quote("${dd}"), Pattern.CASE_INSENSITIVE);
	static final Pattern $DEBUG = Pattern.compile(Pattern.quote("${debug}"), Pattern.CASE_INSENSITIVE);

	static String replace(String line, Pattern placeholder, String value) {
		Matcher m = placeholder.matcher(line);
		if(m.find()) {
			return m.replaceAll(value);
		}
		return line;
	}

	static String unquote(Pattern p) {
		String quoted = p.toString();
		return quoted.substring(2, quoted.length() - 2);
	}
	
	public static String fillTemplate(List<String> lines, String user, String program, String jobName, String dd, String debug) {
		return lines
				.stream()
				.map(String::toUpperCase)
				.map(line -> {
					line = replace(line, $USER, user);
					line = replace(line, $PROGRAM, program);
					line = replace(line, $JOBNAME, jobName);
					line = replace(line, $DD, dd);
					line = replace(line, $DEBUG, debug);
					return line;
				})
				.collect(Collectors.joining("\n"));
	}
	
	private static String generateSteplib(List<String> steplibs) {
		String first = "//STEPLIB   DD DSN=" + steplibs.get(0).toUpperCase() + ",DISP=SHR\n";
		return first +
			steplibs.subList(1, steplibs.size())
				.stream()
				.map(s -> "//          DD DSN=" + s.toUpperCase() + ",DISP=SHR")
				.collect(Collectors.joining("\n", "", ""));
	}

	public static List<String> createJCLTemplate() {
		return createJCLTemplate(generateSteplib(Settings.STEPLIB));
	}
	
	public static List<String> createJCLTemplate(String stepLibs) {
		return new ArrayList<String>(Arrays.asList(
				"//${jobname} JOB ,'" + unquote($USER) + "',",
				"//             SCHENV=TSTSYS,",
				"//             MSGCLASS=Q,",
				"//             NOTIFY=" + unquote($USER) + ",",
				"//             UJOBCORR=" + unquote($USER) + "",
				"//PGMEXEC  EXEC PGM=" + unquote($PROGRAM),
				stepLibs,
				//placeholder for program DDs
				//will be filled before submit
				unquote($DD),
				//generateDDs(ftpClient, userName);
				"//INSPIN      DD *",
				unquote($DEBUG),
				"",
				"/*",
				"//INSPLOG   DD SYSOUT=*",
				//"//INSPCMD   DD DSN=SYS2.DEBUG.COMMANDS,DISP=SHR\n";
				"//CEEOPTS   DD *,DLM='/*'",
				"TEST(,INSPIN,,)",
				"/*"
		));
	}
}
