package dk.bec.unittest.becut.debugscript;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import dk.bec.unittest.becut.Settings;

public class JCLTemplate {
	static final Pattern $USER = Pattern.compile(Pattern.quote("${user}"), Pattern.CASE_INSENSITIVE);
	static final Pattern $PROGRAM = Pattern.compile(Pattern.quote("${program}"), Pattern.CASE_INSENSITIVE);
	static final Pattern $JOBNAME = Pattern.compile(Pattern.quote("${jobname}"), Pattern.CASE_INSENSITIVE);
	static final Pattern $DD = Pattern.compile(Pattern.quote("${dd}"), Pattern.CASE_INSENSITIVE);
	static final Pattern $DEBUG = Pattern.compile(Pattern.quote("${debug}"), Pattern.CASE_INSENSITIVE);
	static final Pattern $INSPLOG = Pattern.compile(Pattern.quote("${insplog}"), Pattern.CASE_INSENSITIVE);

	public static String fillTemplate(
			List<String> lines, 
			String user, 
			String program, 
			String jobName, 
			String dd, 
			String debug,
			String insplog) {
		return lines
				.stream()
				.map(String::toUpperCase)
				.map(line -> {
					line = replace(line, $USER, user);
					line = replace(line, $PROGRAM, program);
					line = replace(line, $JOBNAME, jobName);
					line = replace(line, $DD, dd);
					line = replace(line, $DEBUG, debug);
					line = replace(line, $INSPLOG, insplog);
					return line;
				})
				.collect(Collectors.joining("\n"));
	}
	
	public static String generateSteplib(List<String> steplibs) {
		String first = "//STEPLIB   DD DSN=" + steplibs.get(0).toUpperCase() + ",DISP=SHR\n";
		return first +
			steplibs.subList(1, steplibs.size())
				.stream()
				.map(s -> "//          DD DSN=" + s.toUpperCase() + ",DISP=SHR")
				.collect(Collectors.joining("\n", "", ""));
	}

	public static List<String> generic() {
		return generic(generateSteplib(Settings.STEPLIB));
	}
	
	public static List<String> generic(String stepLibs) {
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
				"//INSPIN      DD *",
				//placeholder for debug statements
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

	public static List<String> recording() {
		return recording(generateSteplib(Settings.STEPLIB));
	}

	public static List<String> recording(String stepLibs) {
		String depthOfCall = String.format("BECUT-%03d-DEPTH", new Random().nextInt(1000));
		return new ArrayList<String>(Arrays.asList(
					"//${jobname} JOB ,'" + unquote($USER) + "',",
					"//             SCHENV=TSTSYS,",
					"//             MSGCLASS=Q,",
					"//             NOTIFY=" + unquote($USER) + ",",
					"//             UJOBCORR=" + unquote($USER) + "",
					"//PGMEXEC  EXEC PGM=" + unquote($PROGRAM),
					stepLibs,
					unquote($DD),
					"//INSPIN    DD *",
					"            SET LOG ON FILE ${insplog};", 
					"            SET DYNDEBUG OFF;", 
					"            77 " + depthOfCall + " PIC 9(9) COMP;",
					"            MOVE 0 TO " + depthOfCall + ";", 
					"            AT CALL * BEGIN;",
					"               IF " + depthOfCall + " < 1 THEN", 
					"                 LIST UNTITLED('AT CALL BEGIN');", 
					"                 LIST CALLS;", 
					"                 LIST TITLED *;", 
					"                 LIST UNTITLED('AT CALL END');", 
					"                 LIST UNTITLED('');", 
					"               END-IF;", 
					"        COMPUTE " + depthOfCall + " =  " + depthOfCall + " + 1;", 
					"               GO;", 
					"            END;", 
					"            AT EXIT * BEGIN;", 
					"               IF " + depthOfCall + " < 2 THEN", 
					"                 LIST UNTITLED('AT EXIT BEGIN');", 
					"                 LIST %PROGRAM;", 
					"                 LIST CALLS;", 
					"                 LIST TITLED *;", 
					"                 LIST UNTITLED('AT EXIT END');", 
					"                 LIST UNTITLED('');", 
					"               END-IF;", 
					"        COMPUTE " + depthOfCall + " =  " + depthOfCall + " - 1;\n", 
					"               GO;",
					"            END;", 
					"            GO;", 
					"            QUIT;", 
					"/*", 
					"//INSPLOG   DD SYSOUT=*", 
					"//CEEOPTS   DD *,DLM='/*'", 
					"TEST(,INSPIN,,)", 
					"/*"
		));
	}	
	
	private static String replace(String line, Pattern placeholder, String value) {
		Matcher m = placeholder.matcher(line);
		if(m.find()) {
			return m.replaceAll(value);
		}
		return line;
	}

	private static String unquote(Pattern p) {
		String quoted = p.toString();
		return quoted.substring(2, quoted.length() - 2);
	}
}
