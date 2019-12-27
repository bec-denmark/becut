package dk.bec.unittest.becut.debugscript;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dk.bec.unittest.becut.compilelist.CobolNodeType;
import dk.bec.unittest.becut.compilelist.TreeUtil;
import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.compilelist.model.DataType;
import dk.bec.unittest.becut.compilelist.model.Record;
import dk.bec.unittest.becut.debugscript.model.LineBreakpoint;
import dk.bec.unittest.becut.debugscript.model.Assertion;
import dk.bec.unittest.becut.debugscript.model.CallType;
import dk.bec.unittest.becut.debugscript.model.Comment;
import dk.bec.unittest.becut.debugscript.model.DebugEntity;
import dk.bec.unittest.becut.debugscript.model.DebugScript;
import dk.bec.unittest.becut.debugscript.model.Goto;
import dk.bec.unittest.becut.debugscript.model.Move;
import dk.bec.unittest.becut.debugscript.model.Perform;
import dk.bec.unittest.becut.debugscript.model.ProgramStartBreakpoint;
import dk.bec.unittest.becut.debugscript.model.Statement;
import dk.bec.unittest.becut.debugscript.model.Step;
import dk.bec.unittest.becut.testcase.BecutTestCaseManager;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import dk.bec.unittest.becut.testcase.model.ExternalCall;
import dk.bec.unittest.becut.testcase.model.Parameter;
import koopa.core.trees.Tree;

public class ScriptGenerator {
	
	
	public static DebugScript generateDebugScript(CompileListing compileListing, BecutTestCase testCase) {
		DebugScript debugScript = new DebugScript(new ArrayList<>());
		List<DebugEntity> debugEntities = debugScript.getEntities();
		debugEntities.add(new Step());

		debugEntities.add(new Comment("Setup preconditions"));
		List<Statement> preConditionStatements = new ArrayList<Statement>();
		for (Parameter parameter : testCase.getPreCondition().getFileSection()) {
			preConditionStatements.addAll(createAssignmentStatements(parameter));
		}
		for (Parameter parameter : testCase.getPreCondition().getWorkingStorage()) {
			preConditionStatements.addAll(createAssignmentStatements(parameter));
		}
		for (Parameter parameter : testCase.getPreCondition().getLocalStorage()) {
			preConditionStatements.addAll(createAssignmentStatements(parameter));
		}
		for (Parameter parameter : testCase.getPreCondition().getLinkageSection()) {
			preConditionStatements.addAll(createAssignmentStatements(parameter));
		}
		debugEntities.add(new ProgramStartBreakpoint(new Perform(preConditionStatements)));
		
		debugEntities.add(new Comment("Setup postconditions"));
		List<Statement> postConditionStatements = new ArrayList<Statement>();
		for (Parameter parameter : testCase.getPostCondition().getFileSection()) {
			postConditionStatements.addAll(createAssertionStatements(parameter));
		}
		for (Parameter parameter : testCase.getPostCondition().getWorkingStorage()) {
			postConditionStatements.addAll(createAssertionStatements(parameter));
		}
		for (Parameter parameter : testCase.getPostCondition().getLocalStorage()) {
			postConditionStatements.addAll(createAssertionStatements(parameter));
		}
		for (Parameter parameter : testCase.getPostCondition().getLinkageSection()) {
			postConditionStatements.addAll(createAssertionStatements(parameter));
		}
		debugEntities.add(new ProgramStartBreakpoint(new Perform(postConditionStatements)));
		
		debugEntities.add(new Step());
		
		for (ExternalCall externalCall: testCase.getExternalCalls()) {
			debugEntities.add(new Comment("Jump over " + externalCall.getDisplayableName().substring(0, Math.min(externalCall.getDisplayableName().length(),  60))));
			DebugEntity debugEntity = null;
			if (externalCall.getCallType() == CallType.SQL) {
				debugEntity = convertSQLCall(compileListing, externalCall);
			}
			else {
				debugEntity = convertExternalCall(compileListing, externalCall);
			}
			debugEntities.add(debugEntity);
		}
		
		return debugScript;
	}
	
	private static DebugEntity convertSQLCall(CompileListing compileListing, ExternalCall externalCall) {
		Tree reconciledSQLCall = reconcileSQLCall(compileListing, externalCall);
		List<Statement> statements = new ArrayList<>();
		for (Parameter parameter: externalCall.getParameters()) {
			statements.addAll(createAssignmentStatements(parameter));
		}
		statements.add(new Goto(findNextStatement(compileListing, reconciledSQLCall)));
		Perform perform = new Perform(statements);
		LineBreakpoint breakpoint = new LineBreakpoint(reconciledSQLCall.getStartPosition().getLinenumber() - 1, perform);
		return breakpoint;
	}
	
	private static DebugEntity convertExternalCall(CompileListing compileListing, ExternalCall externalCall) {
		Tree reconciledExternalCall = reconcileExternalCall(compileListing, externalCall);
		List<Statement> statements = new ArrayList<>();
		for (Parameter parameter: externalCall.getParameters()) {
			statements.addAll(createAssignmentStatements(parameter));
		}
		statements.add(new Goto(findNextStatement(compileListing, reconciledExternalCall)));
		Perform perform = new Perform(statements);
		LineBreakpoint breakpoint = new LineBreakpoint(reconciledExternalCall.getStartPosition().getLinenumber(), perform);
		return breakpoint;
	}
	
	private static List<Statement> createAssignmentStatements(Parameter parameter) {
		List<Statement> returnValues = new ArrayList<>();
		if (!parameter.getValue().equals("")) {
			returnValues.add(new Move(parameter));
		}
		for (Parameter p: parameter.getSubStructure()) {
			returnValues.addAll(createAssignmentStatements(p));
		}
		return returnValues;
	}
	
	private static List<Statement> createAssertionStatements(Parameter parameter) {
		List<Statement> returnValues = new ArrayList<>();
		if (!parameter.getValue().equals("")) {
			returnValues.add(new Assertion(parameter));
		}
		for (Parameter p: parameter.getSubStructure()) {
			returnValues.addAll(createAssertionStatements(p));
		}
		return returnValues;
	}
	
	private static Tree reconcileExternalCall(CompileListing compileListing, ExternalCall externalCall) {
		List<Tree> matches = new ArrayList<>();
		List<Tree> callStatements = TreeUtil.getDescendents(compileListing.getSourceMapAndCrossReference().getAst(), CobolNodeType.CALL_STATEMENT);
		for (Tree callStatement: callStatements) {
			String programName = TreeUtil.stripQuotes(TreeUtil.getDescendents(callStatement, "programName").get(0).getProgramText());
			if (programName.equals(externalCall.getName())) {
				matches.add(callStatement);
			}
		}

		//this is the dream
		if (matches.size() == 1) {
			return matches.get(0);
		}
		
		//TODO: We can't find a program name match. Maybe we should ask the user to identify it.
		if (matches.size() == 0) {
			return null;
		}
		
		//We have multiple matches and need to figure out which to use
		//1) check parameter length
		//2) If there are still multiple matches compare parameter names
		//3) If there are still multiple matches compare parameter types
		//4)If there are still multiple matches compare parameter values?
		//5)Otherwise take the first one?
		

		//1) check parameter length
		List<Tree> parameterLengthMatches = new ArrayList<>();
		for (Tree callStatement: matches) {
			List<Tree> args = TreeUtil.getDescendents(callStatement, CobolNodeType.ARG);
			if (args.size() == externalCall.getParameters().size()) {
				parameterLengthMatches.add(callStatement);
			}
		}

		//this is the dream
		if (parameterLengthMatches.size() == 1) {
			return parameterLengthMatches.get(0);
		}

		//TODO We don't have any calls that match the number of arguments. Maybe we should ask the user to find it?
		if (matches.size() == 0) {
			return null;
		}
		
		//2) If there are still multiple matches compare parameter names
		List<Tree> argNameMatches = new ArrayList<>();
		for (Tree callStatement: parameterLengthMatches) {
			List<Tree> args = TreeUtil.getDescendents(callStatement, CobolNodeType.ARG);
			int count = 0;
			for (Tree arg: args) {
				String argName = BecutTestCaseManager.getArgName(arg);
				for (Parameter parm: externalCall.getParameters()) {
					if (argName.equals(parm.getName())) {
						count++;
						if (count == externalCall.getParameters().size()) {
							argNameMatches.add(callStatement);
						}
					}
				}
			}
		}
		//This is the dream
		if (argNameMatches.size() == 1) {
			return argNameMatches.get(0);
		}
		
		
		//3) If there are still multiple matches compare parameter types
		List<Tree> argTypeMatches = new ArrayList<>();
		for (Tree callStatement: argNameMatches) {
			List<Tree> args = TreeUtil.getDescendents(callStatement, CobolNodeType.ARG);
			int count = 0;
			for (Tree arg: args) {
				String argName = BecutTestCaseManager.getArgName(arg);
				//TODO handle variable in/of. Right now we take the first match
				Record compileListingRecord = compileListing.getDataDivisionMap().getRecord(argName).get(0);
				for (Parameter parm: externalCall.getParameters()) {
					DataType scriptDataType = parm.getDataType();
					Integer scriptDataTypeSize = parm.getSize();
					if (scriptDataType == compileListingRecord.getDataType() || scriptDataTypeSize.equals(compileListingRecord.getSize())) {
						count++;
						if (count == externalCall.getParameters().size()) {
							argTypeMatches.add(callStatement);
						}
					}
				}
			}
		}
		//This is the dream
		if (argTypeMatches.size() == 1) {
			return argTypeMatches.get(0);
		}
		
		return null;
	}

	private static Tree reconcileSQLCall(CompileListing compileListing, ExternalCall externalCall) {
		List<Tree> matches = new ArrayList<>();
		List<Tree> callStatements = TreeUtil.getDescendents(compileListing.getSourceMapAndCrossReference().getAst(), CobolNodeType.CALL_STATEMENT);
		for (Tree callStatement: callStatements) {
			String programName = TreeUtil.stripQuotes(TreeUtil.getDescendents(callStatement, "programName").get(0).getProgramText());
			if (programName.equals("DSNHLI")) {
				matches.add(callStatement);
			}
		}

		//this is the dream
		if (matches.size() == 1) {
			return matches.get(0);
		}
		//We have multiple sql calls and need to find the correct one
		
		//If the line number matches
		for (Tree match: matches) {
			if (match.getStartPosition().getLinenumber() == externalCall.getLineNumber()) {
				return match;
			}
		}
		
		//if the order matches
		//shortest distance?
		
		
		return null;
	}
	
	private static Integer findNextStatement(CompileListing compileListing, Tree externalCall) {
		List<Tree> statements = TreeUtil.getDescendents(compileListing.getSourceMapAndCrossReference().getAst(), CobolNodeType.STATEMENT);
		Collections.sort(statements, new Comparator<Tree>() {
			@Override
			public int compare(Tree t1, Tree t2) {
				return t1.getStartPosition().compareTo(t2.getStartPosition());
			};
		});
		
		/*
		 * TODO this is a naive approach - a check on the containing section would be better
		 * This just returns the statement with the next highest line number
		 */
		
		int count = 0;
		int externalCallLineNumber = externalCall.getStartPosition().getLinenumber();
		for (Tree statement: statements) {
			if (statement.getStartPosition().getLinenumber() == externalCallLineNumber) {
				
				if (count + 1 < statements.size()) {
					return statements.get(count + 1).getStartPosition().getLinenumber();
				}
			}
			count++;
		}
		

		List<Tree> goBackStatement = TreeUtil.getDescendents(compileListing.getSourceMapAndCrossReference().getAst(), CobolNodeType.GOBACK_STATEMENT);
		if (goBackStatement.size() > 0) {
			return goBackStatement.get(0).getStartPosition().getLinenumber();
		}
		return -1;
	}

}
