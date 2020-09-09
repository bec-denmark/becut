package dk.bec.unittest.becut.debugscript;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import dk.bec.unittest.becut.Settings;
import dk.bec.unittest.becut.compilelist.Functions;
import koopa.core.trees.Tree;

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

	public static List<String> recording(Tree ast) {
		return recording(generateJob(ast), generateSteplib(Settings.STEPLIB));
	}

	public static String generateJob(Tree ast) {
		if(Functions.hasDB2Calls(ast)) {
			return 
					"//${jobname} JOB ,'" + unquote($USER) + "',   \n" +
					"//             SCHENV=TSTSYS,                 \n" +
					"//             MSGCLASS=Q,                    \n" +
					"//             NOTIFY=" + unquote($USER) +  ",\n" +
					"//             UJOBCORR=" + unquote($USER) + "\n" +
					"//STEP01 EXEC PGM=IKJEFT01\n" + 
					"//SYSPRINT DD  SYSOUT=*\n" + 
					"//SYSTSPRT DD  SYSOUT=*\n" + 
					"//SYSUDUMP DD  SYSOUT=*\n" + 
					"//SYSTSIN  DD *\n" + 
					"DSN SYSTEM (TD99)\n" + 
					"  RUN PROGRAM (RDZDB2) -\n" + 
					"     PLAN (PLN2) -\n" + 
					"     LIBRARY (COMP.LOAD)\n" + 
					"END\n" + 
					"/*"; 
		} else {
			return 
					"//${jobname} JOB ,'" + unquote($USER) + "',   \n" +
					"//             SCHENV=TSTSYS,                 \n" +
					"//             MSGCLASS=Q,                    \n" +
					"//             NOTIFY=" + unquote($USER) +  ",\n" +
					"//             UJOBCORR=" + unquote($USER) + "\n" +
					"//PGMEXEC  EXEC PGM=" + unquote($PROGRAM);
		}
	}
	
	public static List<String> recording(String job, String stepLibs) {
		String pgm = String.format("BECUT-%03d-PGM", new Random().nextInt(1000));
		return new ArrayList<String>(Arrays.asList(
					job,
					stepLibs,
					unquote($DD),
					"//INSPIN    DD *",
					"        SET LOG ON FILE ${insplog};", 
					"        SET DYNDEBUG OFF;", 
					"        SET LIST TABULAR OFF;",
					"        77 " + pgm + " PIC X(8) ;                                       \r\n" + 
					"        MOVE %PROGRAM TO " + pgm + ";                                   \r\n" + 
					"        AT CALL *                                                       \r\n" + 
					"          BEGIN ;                                                       \r\n" + 
					"            IF %PROGRAM = " + pgm + " THEN                              \r\n" + 
					"              LIST UNTITLED ( 'BEGIN BEFORE CALL' ) ;                   \r\n" + 
					"              LIST CALLS ;                                              \r\n" + 
					"              LIST TITLED * ;                                           \r\n" + 
					"              LIST UNTITLED ( 'END BEFORE CALL' ) ;                     \r\n" + 
					"              LIST UNTITLED ( '' ) ;                                    \r\n" + 
					"              ENABLE AT LINE * ;                                        \r\n" + 
					"            END-IF ;                                                    \r\n" + 
					"            STEP OVER ;                                                 \r\n" + 
					"          END ;                                                         \r\n" + 
					"        AT LINE *                                                       \r\n" + 
					"          BEGIN ;                                                       \r\n" + 
					"            IF %PROGRAM = " + pgm + " THEN                              \r\n" + 
					"              LIST UNTITLED ( 'BEGIN AFTER CALL' ) ;                    \r\n" + 
					"              LIST CALLS ;                                              \r\n" + 
					"              LIST TITLED * ;                                           \r\n" + 
					"              LIST UNTITLED ( 'END AFTER CALL' ) ;                      \r\n" + 
					"              LIST UNTITLED ( '' ) ;                                    \r\n" + 
					"              DISABLE AT LINE * ;                                       \r\n" + 
					"            END-IF ;                                                    \r\n" + 
					"            GO ;                                                        \r\n" + 
					"          END ;                                                         \r\n" + 
					"        AT EXIT *                                                       \r\n" + 
					"          BEGIN ;                                                       \r\n" + 
					"            IF %PROGRAM = " + pgm + " THEN                              \r\n" + 
					"              LIST UNTITLED ( 'BEGIN EXIT' ) ;                          \r\n" + 
					"              LIST CALLS ;                                              \r\n" + 
					"              LIST TITLED * ;                                           \r\n" + 
					"              LIST UNTITLED ( 'END EXIT' ) ;                            \r\n" + 
					"              LIST UNTITLED ( '' ) ;                                    \r\n" + 
					"              DISABLE AT LINE * ;                                       \r\n" + 
					"            END-IF ;                                                    \r\n" + 
					"            GO ;                                                        \r\n" + 
					"          END ;                                                         \r\n" + 
					"        DISABLE AT LINE * ;                                             \r\n" + 
					"        GO ;  \r\n" + 
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
			if(value.isEmpty()) {
				return m.replaceAll("//*");
			} else {
				return m.replaceAll(value);
			}
		}
		return line;
	}

	private static String unquote(Pattern p) {
		String quoted = p.toString();
		return quoted.substring(2, quoted.length() - 2);
	}
}
