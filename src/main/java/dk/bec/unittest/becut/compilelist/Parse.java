package dk.bec.unittest.becut.compilelist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.compilelist.model.CompileOptions;
import dk.bec.unittest.becut.compilelist.model.DataDivisionMap;
import dk.bec.unittest.becut.compilelist.model.DataNamesCrossReference;
import dk.bec.unittest.becut.compilelist.model.InvocationParameters;
import dk.bec.unittest.becut.compilelist.model.ProceduresCrossReference;
import dk.bec.unittest.becut.compilelist.model.ProgramsCrossReference;
import dk.bec.unittest.becut.compilelist.model.SourceMapAndCrossReference;

public class Parse {
	
	private static String paginationTitle = "^P.*\\d{4}-.{3}.*Date.*Time.*Page.* \\d+.*$";
	private static String lineIDSource = "^.*LineID.*----\\+-\\*A-1-B--\\+----2----\\+----3----\\+----4----\\+----5----\\+----6----\\+----7-!--\\+----8.*$";
	private static String lineIDData = "LineID   Data Name                                        Locator    Blk   Structure   Definition      Data Type      Attributes";
	private static String dataHeader = "Source   Hierarchy and                                    Base       Hex-Displacement  Asmblr Data                    Data Def";
	private static String blankLines = "^\\s*$";
	private static String impLines = "                                                                                                  IMP";
	private static String headers = paginationTitle + "|" + lineIDSource + "|" + lineIDData + "|" + dataHeader + "|" + blankLines + "|" + impLines;
	private static String dataCrossReferenceEndMarker = "Context usage is indicated by the letter preceding a procedure-name reference";
	private static Predicate<String> paginationHeadersPredicate = Pattern.compile(headers).asPredicate().negate(); 
	
	//Remove pagination titles from compile listing
	public static List<String> clearPaginationHeaders(List<String> compileListing) {
		return compileListing.stream().filter(paginationHeadersPredicate).collect(Collectors.<String>toList());
	}
	
	public static CompileListing parse(File file) throws FileNotFoundException {
		return parse(new FileInputStream(file));
	}
	
	public static CompileListing parse(InputStream inputStream) {
		List<String> compileListing = new ArrayList<String>();
		try (Scanner s = new Scanner(inputStream, "UTF-8")) {
			while (s.hasNextLine()) {
				compileListing.add(s.nextLine());
			}
		}
		return parse(compileListing);
	}

	public static CompileListing parse(List<String> compileListing) {
		List<String> cleanCompileList = clearPaginationHeaders(compileListing);
		int count = 0;
		int invocationStart = 0;
		int invocationEnd = 0;
		int compileOptionStart = 0;
		int compileOptionEnd = 0;
		int sourceStart = 0;
		int sourceEnd = 0;
		int dataNamesStart = 0;
		int dataNamesEnd = 0;
		boolean dataNamesStartFound = false;
		int procedureStart = 0;
		int procedureEnd = 0;
		boolean procedureStartFound = false;
		int programStart = 0;
		int programEnd = 0;
		boolean programStartFound = false;
		int dataDivisionStart = 0;
		int dataDivisionEnd = 0;
		boolean dataDivisionEndFound = false;

		for (int i = 0; i < cleanCompileList.size(); i++) {
			String line = cleanCompileList.get(i);
			if ("Invocation parameters:".equals(line)) {
				invocationStart = count + 1;
			}
			else if (line.startsWith("Options in effect")) {
				invocationEnd = count;
				compileOptionStart = count + 1;
			}
			else if (line.matches("^.*ID.* DIVISION.*$")) {
				compileOptionEnd = count - 1;
				sourceStart = count;
			}
			else if (line.startsWith("An \"M\" preceding a data-name reference indicates that the data-name is modified by this reference.")) {
				if (cleanCompileList.get(i-1).startsWith("*/")) {
					sourceEnd = count -1;
				} else {
					sourceEnd = count;
				}
			}
			else if (!dataNamesStartFound && line.startsWith(" Defined   Cross-reference of data names")) {
				dataNamesStart = count + 1;
				dataNamesStartFound = true;
			}
			else if (line.startsWith(dataCrossReferenceEndMarker)) {
				if (dataNamesStart >= count - 1) {
					dataNamesEnd = dataNamesStart;
				} else {
					dataNamesEnd = count - 1;
				}
				dataNamesStartFound = false;
			}
			else if (!procedureStartFound && line.startsWith(" Defined   Cross-reference of procedures")) {
				procedureStart = count + 1;
				procedureStartFound = true;
			}
			else if (!programStartFound && line.startsWith(" Defined   Cross-reference of programs")) {
				if (dataNamesStartFound) {
					if (dataNamesStart >= count) {
						dataNamesEnd = dataNamesStart;
					} else {
						dataNamesEnd = count - 1;
					}
				}
				if (procedureStartFound) {
					procedureEnd = count;
				}
				programStart = count + 1;
				programStartFound = true;
			}
			else if (line.startsWith("Data Division Map")) {
				programEnd = count;
				dataDivisionStart = count + 1;
			}
			else if (!dataDivisionEndFound && (line.startsWith("PROGRAM GLOBAL TABLE BEGINS") || line.startsWith("Messages    Total    Informational    Warning    Error    Severe    Terminating") || line.startsWith("LineID  Message code  Message text"))) {
				dataDivisionEnd = count;
				dataDivisionEndFound = true;
			}

			count++;
		}
		
		InvocationParameters invocationParameters = new InvocationParameters(cleanCompileList.subList(invocationStart, invocationEnd));
		CompileOptions compileOptions = new CompileOptions(cleanCompileList.subList(compileOptionStart, compileOptionEnd));
		SourceMapAndCrossReference sourceMapAndCrossReference = new SourceMapAndCrossReference(cleanCompileList.subList(sourceStart, sourceEnd));
		DataNamesCrossReference dataNamesCrossReference = new DataNamesCrossReference(cleanCompileList.subList(dataNamesStart, dataNamesEnd));
		ProceduresCrossReference proceduresCrossReference = new ProceduresCrossReference(cleanCompileList.subList(procedureStart, procedureEnd));
		ProgramsCrossReference programsCrossReference = new ProgramsCrossReference(cleanCompileList.subList(programStart, programEnd));
		DataDivisionMap dataDivisionMap = new DataDivisionMap(cleanCompileList.subList(dataDivisionStart, dataDivisionEnd));
		return new CompileListing(invocationParameters, compileOptions, sourceMapAndCrossReference, dataNamesCrossReference, proceduresCrossReference, programsCrossReference, dataDivisionMap, compileListing);
		
	}

}
