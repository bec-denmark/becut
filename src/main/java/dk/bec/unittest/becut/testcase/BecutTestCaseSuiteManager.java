package dk.bec.unittest.becut.testcase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import dk.bec.unittest.becut.Constants;
import dk.bec.unittest.becut.Either;
import dk.bec.unittest.becut.compilelist.CobolNodeType;
import dk.bec.unittest.becut.compilelist.Functions;
import dk.bec.unittest.becut.compilelist.Parse;
import dk.bec.unittest.becut.compilelist.TreeUtil;
import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.compilelist.model.DataNameReference;
import dk.bec.unittest.becut.compilelist.model.DataType;
import dk.bec.unittest.becut.compilelist.model.Record;
import dk.bec.unittest.becut.compilelist.sql.SQLParse;
import dk.bec.unittest.becut.debugscript.model.CallType;
import dk.bec.unittest.becut.recorder.model.SessionCall;
import dk.bec.unittest.becut.recorder.model.SessionCallPart;
import dk.bec.unittest.becut.recorder.model.SessionRecord;
import dk.bec.unittest.becut.recorder.model.SessionRecording;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import dk.bec.unittest.becut.testcase.model.BecutTestSuite;
import dk.bec.unittest.becut.testcase.model.ExternalCall;
import dk.bec.unittest.becut.testcase.model.ExternalCallIteration;
import dk.bec.unittest.becut.testcase.model.Parameter;
import dk.bec.unittest.becut.testcase.model.ParameterLiteral;
import dk.bec.unittest.becut.testcase.model.PostCondition;
import dk.bec.unittest.becut.testcase.model.PreCondition;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import koopa.core.trees.Tree;
import koopa.core.trees.jaxen.Jaxen;

public class BecutTestCaseSuiteManager {

	private static ObjectMapper mapper = new ObjectMapper();

	private BecutTestCaseSuiteManager() {
	}

	public static BecutTestSuite createTestCaseSuiteFromCompileListing(CompileListing compileListing) {
		String testCaseName = "becut-" + compileListing.getProgramName();
		String testCaseId = "becut-" + compileListing.getProgramName() + "-" + UUID.randomUUID().toString();
		return createTestCaseSuiteFromCompileListing(compileListing, testCaseName, testCaseId);
	}

	public static BecutTestSuite createTestCaseSuiteFromCompileListing(CompileListing compileListing, String testCaseName,
			String testCaseId) {
		BecutTestSuite becutTestCaseSuite = new BecutTestSuite();
		becutTestCaseSuite.setCompileListing(compileListing);
		
		BecutTestCase becutTestCase = new BecutTestCase();
		becutTestCase.setProgramName(compileListing.getProgramName());
		becutTestCase.setTestCaseName(testCaseName);
		becutTestCase.setTestCaseId(testCaseId);
		
		Map<String, String> fileControlAssignment = compileListing.getSourceMapAndCrossReference().getFileControlAssignment();
		becutTestCase.setFileControlAssignments(fileControlAssignment);
		
		List<Tree> callStatements = TreeUtil.getDescendents(compileListing.getSourceMapAndCrossReference().getAst(),
				CobolNodeType.CALL_STATEMENT);
		for (Tree callStatement : callStatements) {
			String callProgramName = Functions
					.stripQuotes(TreeUtil.getDescendents(callStatement, "programName").get(0).getProgramText());
			// We are skipping the SQL generated calls
			if (!Constants.IBMHostVariableMemoryAllocationPrograms.contains(callProgramName)) {
				becutTestCase.getExternalCalls().add(createExternalCall(callStatement, compileListing));
			}
		}
		List<Parameter> fileSectionParms = parseRecordsFromSection(compileListing, CobolNodeType.FILE_SECTION);
		List<Parameter> workingStorageParms = parseRecordsFromSection(compileListing, CobolNodeType.WORKING_STORAGE);
		List<Parameter> localStorageParms = parseRecordsFromSection(compileListing,
				CobolNodeType.LOCAL_STORAGE_SECTION);
		List<Parameter> linkageSectionParms = parseRecordsFromSection(compileListing, CobolNodeType.LINKAGE_SECTION);

		PreCondition preCondition = new PreCondition();
		preCondition.setFileSection(fileSectionParms);
		preCondition.setWorkingStorage(workingStorageParms);
		preCondition.setLocalStorage(localStorageParms);
		preCondition.setLinkageSection(linkageSectionParms);
		becutTestCase.setPreCondition(preCondition);

		fileSectionParms = parseRecordsFromSection(compileListing, CobolNodeType.FILE_SECTION);
		workingStorageParms = parseRecordsFromSection(compileListing, CobolNodeType.WORKING_STORAGE);
		localStorageParms = parseRecordsFromSection(compileListing, CobolNodeType.LOCAL_STORAGE_SECTION);
		linkageSectionParms = parseRecordsFromSection(compileListing, CobolNodeType.LINKAGE_SECTION);

		PostCondition postCondition = new PostCondition();
		postCondition.setFileSection(fileSectionParms);
		postCondition.setWorkingStorage(workingStorageParms);
		postCondition.setLocalStorage(localStorageParms);
		postCondition.setLinkageSection(linkageSectionParms);
		becutTestCase.setPostCondition(postCondition);

		becutTestCaseSuite.add(becutTestCase);
		
		return becutTestCaseSuite;
	}

	public static BecutTestCase createTestCaseFromSessionRecording(CompileListing compileListing, SessionRecording sessionRecording) {
		BecutTestSuite becutTestCaseSuite = createTestCaseSuiteFromCompileListing(compileListing);
		BecutTestCase becutTestCase = becutTestCaseSuite.get(0);

		Map<Integer, ExternalCall> callCache = new HashMap<>();

a:		for (SessionCall sessionCall : sessionRecording.getSessionCalls()) {
			List<Tree> callStatements = TreeUtil.getDescendents(
					compileListing.getSourceMapAndCrossReference().getAst(), CobolNodeType.CALL_STATEMENT);
			for (Tree callStatement : callStatements) {
				if(callStatement.getStartPosition().getLinenumber() == sessionCall.getLineNumber()) {
					String callProgramName = Jaxen.evaluate(callStatement, "programName//text()")
							.stream()
							.map(Tree.class::cast)
							.map(Tree::getText).collect(Collectors.joining());
					if (Constants.IBMHostVariableMemoryAllocationPrograms.contains(callProgramName)) {
						continue a;
					}
				}
			}			
			
			Integer lineNumber = sessionCall.getLineNumber();
			// first iteration
			if (!callCache.containsKey(lineNumber)) {
				ExternalCall externalCall = becutTestCase.getExternalCalls().stream()
						.filter(ec -> ec.getLineNumber().equals(lineNumber))
						.collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
							if (list.isEmpty())
								throw new SessionRecordingException(
										"Can't match the call; a mismatch between the listing and the program?");
							if (list.size() > 1)
								throw new SessionRecordingException(
										"An inconclusive match: more than one call in the line " + lineNumber);
							return list.get(0);
						}));

				externalCall.setStatementId(sessionCall.getStatementId());

				externalCall.getFirstIteration().getParameters()
						.forEach(p -> setParameterValue(p, sessionCall.getAfter()));

				callCache.put(lineNumber, externalCall);
			} else {
				ExternalCall externalCall = callCache.get(lineNumber);
				List<Parameter> iterationParameters = externalCall.getFirstIteration().getParameters().stream()
						.map(Parameter::copyWithNoValues).collect(Collectors.toList());

				iterationParameters.forEach(p -> setParameterValue(p, sessionCall.getAfter()));

				externalCall.addIteration(iterationParameters);
			}
		}
		
		becutTestCase.getPostCondition().getWorkingStorage()
			.forEach(p -> setParameterValue(p, sessionRecording.getAfter()));
		
		return becutTestCase;
	}

	private static void setParameterValue(Parameter p, SessionCallPart scp) {
		SessionRecord sr = scp.getSessionRecord(p.getLevel(), p.getName());
		if (!DataType.GROUP.equals(p.getDataType()) && !p.getName().equals("FILLER") 
				&& !DataType.EIGHTYEIGHT.equals(p.getDataType())) {
			if(sr == null) {
				//TODO handle 'table' type
				System.err.println("Cannot find session record for " + p);
			} else {
				p.setValue(sr.getValue());
			}
		}
		p.getSubStructure().forEach(sp -> setParameterValue(sp, scp));
	}

	/**
	 * Run through records in the DataDivision and filter records that is declared
	 * between start and end line of a section
	 * 
	 * @param compileListing - A tree containing the source-code
	 * @param dataSection    - CobolNodeType describing the desired section
	 * @return List of parameters in the dataSection
	 */
	public static List<Parameter> parseRecordsFromSection(CompileListing compileListing, CobolNodeType dataSection) {
		List<Parameter> parameterList = new ArrayList<Parameter>();

		List<Tree> sourceSectionList = TreeUtil.getDescendents(compileListing.getSourceMapAndCrossReference().getAst(),
				dataSection);

		if (sourceSectionList.size() == 0) {
			return parameterList;
		}
		Tree sourceSection = sourceSectionList.get(0);

		compileListing.getDataDivisionMap().getRecords().values()
			.stream()
			.filter(i -> sourceSection.getStartPosition().getLinenumber() <= i.getLineNumber()
						&& i.getLineNumber() <= sourceSection.getEndPosition().getLinenumber())
			.forEach(i -> parameterList.add(new Parameter(i)));

		return parameterList;
	}

	public static Either<BecutTestSuite, String> loadTestCaseSuite(Path folder) {
		BecutTestSuite becutTestCaseSuite = new BecutTestSuite();
		
		try {
			CompileListing compileListing = Parse.parse(Paths.get(folder.toString(), "compile_listing.txt").toFile());
			becutTestCaseSuite.setCompileListing(compileListing);
			
			Path suite = Paths.get(folder.toString(), "suite.txt");
			if(!Files.exists(suite)) {
				return Either.right(suite + " is missing.");
			}
			
			Files.readAllLines(suite).forEach(line -> {
				BecutTestCase becutTestCase = new BecutTestCase();
				try(FileInputStream fileInputStream = new FileInputStream(Paths.get(folder.toString(), line, "test_case.json").toFile())) {
					becutTestCase = mapper.readValue(fileInputStream, BecutTestCase.class);
					becutTestCase.setTestCaseName(line);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				becutTestCaseSuite.add(becutTestCase);
			});
		} catch (IOException e) {
			return Either.right(e.getMessage());
		}
		
		return Either.left(becutTestCaseSuite);
	}

	public static void saveTestCaseSuite(BecutTestSuite becutTestCaseSuite, Path folder) {
		try {
			Files.write(Paths.get(folder.toString(), "compile_listing.txt"), 
					becutTestCaseSuite.getCompileListing().getOriginalSource(),
					Charset.defaultCharset());
			
			List<String> testCaseFolders = new ArrayList<>();
			for(BecutTestCase testCase : becutTestCaseSuite) {
				Path path = Paths.get(folder.toString(), testCase.getTestCaseName());
				if(!Files.exists(path)) {
					Files.createDirectory(path);
				}
				testCaseFolders.add(testCase.getTestCaseName());
				saveTestCase(testCase, path);
			}

			Path debugScriptPath = BECutAppContext.getContext().getTestScriptPath();
    		if (Files.exists(debugScriptPath)) {
				Files.copy(debugScriptPath, 
						Paths.get(folder.toString(), debugScriptPath.getFileName().toString()), 
						StandardCopyOption.REPLACE_EXISTING);
    		}
			BECutAppContext.getContext().setTestSuiteFolder(folder);
			//suite.txt defines which subfolders are tests definitions and 
			//what is the order of execution (should it be really important?)
			Files.write(Paths.get(folder.toString(), "suite.txt"), testCaseFolders);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void saveTestCase(BecutTestCase becutTestCase, Path folder) {
		assert Files.isDirectory(folder);
		try {
			Path unitTestSuiteFolder = BECutAppContext.getContext().getUnitTestSuiteFolder();
			becutTestCase.getFileControlAssignments().entrySet()
				.stream()
				.map(e -> Paths.get(unitTestSuiteFolder.toString(), becutTestCase.getTestCaseName(), e.getValue() + ".txt"))
				.filter(p -> Files.exists(p))
				.forEach(p -> {
					try {
						Files.copy(p, 
								Paths.get(folder.toString(), p.getFileName().toString()), 
								StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException ioe) {
						throw new RuntimeException(ioe);
					}
				});
			mapper.writerWithDefaultPrettyPrinter().writeValue(
					Paths.get(folder.toString(), "test_case.json").toFile(), becutTestCase);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static ExternalCall createExternalCall(Tree callStatement, CompileListing compileListing) {
		String callProgramName = Functions.stripQuotes(
				TreeUtil.getDescendents(callStatement, CobolNodeType.PROGRAM_NAME).get(0).getProgramText());
		// FIXME This is currently getting the absolute line number (from the expanded
		// source).
		// Figure out how to get the original line numbers that the user actually sees.
		int lineNumber = callStatement.getStartPosition().getLinenumber();
		ExternalCall externalCall = new ExternalCall(callProgramName, lineNumber, CallType.UNKNOWN,
				createParameters(callStatement, compileListing));
		String iterationName = externalCall.getFirstIteration().getName();

		if (!Constants.IBMHostVariableMemoryAllocationPrograms.contains(callProgramName)) {
			// TODO It is here the addition to CICS EXEC can start
			if (Constants.IBMSQLPrograms.contains(callProgramName)) {
				// TODO implement handling of SQL - don't show the cobol call but show the sql
				// instead
				externalCall.setCallType(CallType.SQL);
				// Remove the real call and add host variables instead
				String sql = SQLParse.findSQLStatement(callStatement);
				externalCall.setDisplayableName(sql.replaceAll("\\n", ""));
				ExternalCallIteration externalCallIteration = externalCall.getIterations().get(iterationName);
				externalCallIteration.getParameters().clear();
				try {
					List<Record> hostVariables = SQLParse.getHostVariables(sql, compileListing);
					for (Record hostVariable : hostVariables) {
						externalCallIteration.getParameters().add(new Parameter(hostVariable));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Add sqlca
				List<DataNameReference> sqlca = compileListing.getDataNamesCrossReference()
						.getDataNameReferencesByName().get("SQLCA");
				if (sqlca.size() == 1) {
					Parameter SQLCA = new Parameter(
							compileListing.getDataDivisionMap().getRecord(sqlca.get(0).getLineNumber()));
					externalCallIteration.getParameters().add(SQLCA);
				} else {
					// TODO We have no SQLCA or more than one. What do I do?
				}
			} else {
				// normal call - figure out if static or dynamic
				externalCall.setCallType(CallType.DYNAMIC);
				externalCall.setDisplayableName(String.format("%06d ", lineNumber) + externalCall.toString());
			}
		}
		return externalCall;
	}

	private static List<Parameter> createParameters(Tree callStatement, CompileListing compileListing) {
		List<Parameter> parameters = new ArrayList<Parameter>();
		List<Tree> args = TreeUtil.getDescendents(callStatement, CobolNodeType.ARG);
		for (Tree arg : args) {
			// TODO handle "variable in/of"
			String argName = getArgName(arg);
			List<DataNameReference> refs = compileListing.getDataNamesCrossReference().getDataNameReferencesByName()
					.get(argName);
			// We have a literal in the parameter
			if (refs == null) {
				ParameterLiteral parm = new ParameterLiteral(argName);
				parameters.add(parm);
			} else if (refs.size() == 1) {
				Parameter parm = new Parameter(
						compileListing.getDataDivisionMap().getRecord(refs.get(0).getLineNumber()));
				parameters.add(parm);
			} else if (refs.size() > 1) {
				// TODO We have found multiple references. In the future maybe we can ask which
				// one to use. For now we take the first one
				Parameter parm = new Parameter(
						compileListing.getDataDivisionMap().getRecord(refs.get(0).getLineNumber()));
				parameters.add(parm);
			} else {
				// TODO We didn't find the reference. Handle this case
			}
		}
		return parameters;
	}

	public static String getArgName(Tree arg) {

		// Variable name
		List<Tree> argNames = TreeUtil.getDescendents(arg, CobolNodeType.QUALIFIED_DATA_NAME);
		if (argNames.isEmpty()) {
			// This is actaully a literal
			// TODO mark literals as something that can't be changed in the test case
			argNames = TreeUtil.getDescendents(arg, CobolNodeType.LITERAL);
		}
		//
		String argProgramText = argNames.get(0).getAllText();
		return handleContinuation(argProgramText);
	}

	private static String handleContinuation(String arg) {
		String name = "";
		if (arg.contains("\n")) {
			String[] lines = arg.split("\n");
			// TODO this only handles continuations that are 1 line long
			if (lines[1].length() > 6 && lines[1].charAt(6) == '-') {
				name = lines[0].trim() + lines[1].substring(7).trim().split("\\s+")[0];
			} else {
				name = arg.split("\\s+")[0];
			}
		} else {
			name = arg.split("\\s+")[0];
		}
		return name;
	}

}
