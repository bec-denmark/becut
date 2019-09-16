package dk.bec.unittest.becut.integrationtests;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import dk.bec.unittest.becut.compilelist.CobolNodeType;
import dk.bec.unittest.becut.compilelist.Parse;
import dk.bec.unittest.becut.compilelist.TreeUtil;
import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.compilelist.model.Record;
import dk.bec.unittest.becut.debugscript.model.CallType;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import dk.bec.unittest.becut.testcase.model.ExternalCall;
import dk.bec.unittest.becut.testcase.model.Parameter;
import junit.framework.TestCase;
import koopa.core.trees.Tree;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

public class GenerateTestScriptIT extends TestCase {

	public void testGenerateTestScriptMAT510RS() {
		File file = new File("./src/test/resources/compilelistings/mat510rs_compile_listing.txt");
		try {
			CompileListing compileListing = Parse.parse(file);
			
			BecutTestCase testCase = new BecutTestCase();
			testCase.setTestCaseName("TestCaseName");
			testCase.setTestCaseId("TestCaseId");
			Tree ast = compileListing.getSourceMapAndCrossReference().getAst();
			String programName = TreeUtil.getFirst(ast, "programName").getProgramText();
			testCase.setProgramName(programName);
			
			List<Parameter> parameters = new ArrayList<Parameter>();

			Tree externalCallTree = TreeUtil.getFirst(ast, "callStatement");
			List<Tree> args = TreeUtil.getDescendents(externalCallTree, CobolNodeType.ARG);
			for (Tree arg: args) {
				//TODO currently not supporting "record in/of parent" syntax
				String argName = arg.getAllText();
				List<Record> matchingRecords = compileListing.getDataDivisionMap().getRecord(argName);
				if (matchingRecords.size() == 1) {
					parameters.add(new Parameter(matchingRecords.get(0)));
				}
			}

			ExternalCall externalCall = new ExternalCall("TMAT5110", "TMAT5110", externalCallTree.getStartPosition().getLinenumber(), CallType.DYNAMIC, parameters);
			testCase.addExternalCall(externalCall);
			
			
			assertThat(testCase.getExternalCalls(), hasSize(1));
			assertThat(testCase.getExternalCalls().get(0).getParameters(), hasSize(1));
			assertThat(testCase.getExternalCalls().get(0).getParameters().get(0).getSubStructure(), hasSize(3));
			assertThat(testCase.getExternalCalls().get(0).getParameters().get(0).getSubStructure().get(2).getSubStructure(), hasSize(3));
			assertThat(testCase.getExternalCalls().get(0).getParameters().get(0).getSubStructure().get(2).getSubStructure().get(0).getSubStructure(), hasSize(2));
			assertThat(testCase.getExternalCalls().get(0).getParameters().get(0).getSubStructure().get(2).getSubStructure().get(0).getSubStructure().get(0).getSubStructure(), hasSize(0));


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}

}
