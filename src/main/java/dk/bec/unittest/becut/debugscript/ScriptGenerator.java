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
import dk.bec.unittest.becut.debugscript.model.Addition;
import dk.bec.unittest.becut.debugscript.model.CallType;
import dk.bec.unittest.becut.debugscript.model.DebugEntity;
import dk.bec.unittest.becut.debugscript.model.DebugScript;
import dk.bec.unittest.becut.debugscript.model.Perform;
import dk.bec.unittest.becut.debugscript.model.ProgramStartBreakpoint;
import dk.bec.unittest.becut.debugscript.model.ProgramTerminationBreakpoint;
import dk.bec.unittest.becut.debugscript.model.Step;
import dk.bec.unittest.becut.debugscript.model.conditional.Conditional;
import dk.bec.unittest.becut.debugscript.model.conditional.ConditionalLeaf;
import dk.bec.unittest.becut.debugscript.model.conditional.EqualsConditional;
import dk.bec.unittest.becut.debugscript.model.conditional.GreaterThanConditional;
import dk.bec.unittest.becut.debugscript.model.statement.Assertion;
import dk.bec.unittest.becut.debugscript.model.statement.Comment;
import dk.bec.unittest.becut.debugscript.model.statement.Compute;
import dk.bec.unittest.becut.debugscript.model.statement.Goto;
import dk.bec.unittest.becut.debugscript.model.statement.If;
import dk.bec.unittest.becut.debugscript.model.statement.LineBreakpoint;
import dk.bec.unittest.becut.debugscript.model.statement.Move;
import dk.bec.unittest.becut.debugscript.model.statement.Statement;
import dk.bec.unittest.becut.debugscript.model.variable.Literal;
import dk.bec.unittest.becut.debugscript.model.variable.Pic9Comp;
import dk.bec.unittest.becut.testcase.BecutTestCaseManager;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import dk.bec.unittest.becut.testcase.model.ExternalCall;
import dk.bec.unittest.becut.testcase.model.ExternalCallIteration;
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
		debugEntities.add(new ProgramTerminationBreakpoint(new Perform(postConditionStatements), compileListing.getProgramName()));
		
		debugEntities.add(new Step());
		
		for (ExternalCall externalCall: testCase.getExternalCalls()) {
			debugEntities.add(new Comment("Jump over " + externalCall.getDisplayableName().substring(0, Math.min(externalCall.getDisplayableName().length(),  60))));
			DebugEntity debugEntity = null;
			if (externalCall.getCallType() == CallType.SQL) {
				debugEntity = convertSQLCall(compileListing, externalCall);
				debugEntities.add(debugEntity);
			}
			else {
				debugEntities.addAll(convertExternalCall(compileListing, externalCall, debugScript));
			}
		}
		
		return debugScript;
	}
	
	public static DebugScript generateParameterRecordingScript(CompileListing compileListing) {
		DebugScript debugScript = new DebugScript(new ArrayList<>());
		List<DebugEntity> debugEntities = debugScript.getEntities();
		debugEntities.add(new Step());
		
		return debugScript;
	}
	
	//TODO Add support for multiple calls
	private static DebugEntity convertSQLCall(CompileListing compileListing, ExternalCall externalCall) {
		Tree reconciledSQLCall = reconcileSQLCall(compileListing, externalCall);
		List<Statement> statements = new ArrayList<>();
		for (ExternalCallIteration iteration: externalCall.getIterations().values()) {
			statements.addAll(createAssignmentStatements(iteration));
		}
		statements.add(new Goto(findNextStatement(compileListing, reconciledSQLCall)));
		Perform perform = new Perform(statements);
		LineBreakpoint breakpoint = new LineBreakpoint(reconciledSQLCall.getStartPosition().getLinenumber() - 1, perform);
		return breakpoint;
	}
	
	private static List<DebugEntity> convertExternalCall(CompileListing compileListing, ExternalCall externalCall, DebugScript debugScript) {
		List<DebugEntity> debugEntities = new ArrayList<DebugEntity>();
		Tree reconciledExternalCall = reconcileExternalCall(compileListing, externalCall);
		// There is only one iteration so we don't need an if statement, we use this one for each iteration
		if (externalCall.getIterations().size() == 1) {
			LineBreakpoint breakpoint = createBreakpoint(externalCall.getFirstIteration(), compileListing, reconciledExternalCall);
			debugEntities.add(breakpoint);
		}
		// We need to create if statements for each iteration
		else {
			Pic9Comp counter = new Pic9Comp("BECUT-IC-" + externalCall.getLineNumber() + "-" + externalCall.getName(), 9, "0");
			debugScript.getVariableDeclarations().add(counter);
			Perform perform = new Perform();
			LineBreakpoint breakpoint = new LineBreakpoint(reconciledExternalCall.getStartPosition().getLinenumber(), perform);
			
			for (ExternalCallIteration iteration: externalCall.getIterations().values()) {
				// We need a complex if statement to cover the single iteration and all other iterations
				if (iteration.isDefault()) {
					Conditional counterCheck = new EqualsConditional(counter, new ConditionalLeaf(iteration.getNumericalOrder().toString()));
					Conditional defaultCheck = new GreaterThanConditional(counter, new ConditionalLeaf(String.valueOf(externalCall.getIterations().size() - 1)));
					List<Statement> statements = new ArrayList<>();
					statements.addAll(createAssignmentStatements(iteration));
					If ifStatement = new If(counterCheck, statements);
					perform.getStatements().add(ifStatement);
					If defaultIfStatement = new If(defaultCheck, statements);
					perform.getStatements().add(defaultIfStatement);
				}
				else {
					Conditional conditional = new EqualsConditional(counter, new ConditionalLeaf(iteration.getNumericalOrder().toString()));
					List<Statement> statements = new ArrayList<>();
					statements.addAll(createAssignmentStatements(iteration));
					If ifStatement = new If(conditional, statements);
					perform.getStatements().add(ifStatement);
				}
			}
			perform.getStatements().addAll(incrementCounter(counter));
			perform.getStatements().add(new Goto(findNextStatement(compileListing, reconciledExternalCall)));
			debugEntities.add(breakpoint);
			
		}
		return debugEntities;
	}
	
	private static LineBreakpoint createBreakpoint(ExternalCallIteration iteration, CompileListing compileListing, Tree reconciledExternalCall) {
		List<Statement> statements = new ArrayList<>();
		statements.addAll(createAssignmentStatements(iteration));
		statements.add(new Goto(findNextStatement(compileListing, reconciledExternalCall)));
		Perform perform = new Perform(statements);
		return new LineBreakpoint(reconciledExternalCall.getStartPosition().getLinenumber(), perform);
		
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
	
	private static List<Statement> createAssignmentStatements(ExternalCallIteration callIteration) {
		List<Statement> returnValues = new ArrayList<>();
		for (Parameter parameter: callIteration.getParameters()) {
			if (!parameter.getValue().equals("")) {
				returnValues.add(new Move(parameter));
			}
			for (Parameter p: parameter.getSubStructure()) {
				returnValues.addAll(createAssignmentStatements(p));
			}
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
	
	private static List<Statement> incrementCounter(Pic9Comp counter) {
		List<Statement> statements = new ArrayList<Statement>();
		Compute compute = new Compute(counter, new Addition(counter, new Literal("1")));
		statements.add(compute);
		return statements;
		
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
			if (args.size() == externalCall.getFirstIteration().getParameters().size()) {
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
				for (Parameter parm: externalCall.getFirstIteration().getParameters()) {
					if (argName.equals(parm.getName())) {
						count++;
						if (count == externalCall.getFirstIteration().getParameters().size()) {
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
				for (Parameter parm: externalCall.getFirstIteration().getParameters()) {
					DataType scriptDataType = parm.getDataType();
					Integer scriptDataTypeSize = parm.getSize();
					if (scriptDataType == compileListingRecord.getDataType() || scriptDataTypeSize.equals(compileListingRecord.getSize())) {
						count++;
						if (count == externalCall.getFirstIteration().getParameters().size()) {
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
	
	public static Integer findNextStatement(CompileListing compileListing, Tree externalCall) {
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
