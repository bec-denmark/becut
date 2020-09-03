package dk.bec.unittest.becut.compilelist;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.Test;

import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.compilelist.model.Functions;
import koopa.core.trees.Tree;

public class ParseTest {
	@Test
	public void testCreateCompileListMAT510RS() throws Exception {
		//File file = new File("./src/test/resources/compilelistings/mat510rs_compile_listing.txt");
		File file = new File("C:/temp/sql-suite/compile_listing.txt");
		CompileListing compileListing = Parse.parse(file);
		assertNotNullCompileListing(compileListing);
	}

	@Test
	public void testCreateCompileListMAT512RS() throws FileNotFoundException {
		File file = new File("./src/test/resources/compilelistings/mat512rs_compile_listing.txt");
		CompileListing compileListing = Parse.parse(file);
		assertNotNullCompileListing(compileListing);
	}

	@Test
	public void testCreateCompileListSyntaxCheck() throws FileNotFoundException {
		File file = new File("./src/test/resources/compilelistings/mat514rs_syntaxcheck_listing.txt");
		try {
			CompileListing compileListing = Parse.parse(file);
			assertNotNullCompileListing(compileListing);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCreateCompileListMAT560() throws FileNotFoundException {
		File file = new File("./src/test/resources/compilelistings/mat561_compile_listing.txt");
		CompileListing compileListing = Parse.parse(file);
		assertNotNullCompileListing(compileListing);
		Pattern p1 = Pattern.compile(" {2}\\d{6}C\\s+\\d.*");
		Pattern p2 = Pattern.compile(" {2}(\\d{6}).{9}(\\d{6})\\s+.*");
		Map<Integer, Integer> map = new HashMap<>();
		String source = compileListing.getSourceMapAndCrossReference().getOriginalSource().stream()
				.filter(line -> !p1.matcher(line).matches()).map(line -> {
					Matcher m = p2.matcher(line);
					if (m.find()) {
						map.put(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2).substring(0, 5)));
					} else {
						System.out.println(line);
					}
					return line;
				}).map(line -> line.substring(17)).map(line -> line.substring(0, Math.min(line.length(), 79)))
				.collect(Collectors.joining("\n"));

		Tree ast = compileListing.getSourceMapAndCrossReference().getAst();
		List<Tree> calls = TreeUtil.getDescendents(ast, CobolNodeType.CALL_STATEMENT);
		for (Tree call : calls) {
			System.out.println(call.getStartPosition().getLinenumber());
		}
	}

	@Test
	public void testShouldRegisterFileControlNames() throws Exception {
		File file = new File("src/test/resources/compilelistings/JOB06352.5");
		CompileListing compileListing = Parse.parse(file);
		Map<String, String> assignements = compileListing.getSourceMapAndCrossReference().getFileControlAssignment();
		assertThat(assignements, notNullValue());
		assertThat(assignements, hasKey("NUM-LIST"));
		assertThat(assignements, hasEntry("NUM-LIST", "INPUT1"));
	}

	@Test
	public void testShouldRegisterFileControlNamesWithNumbers() throws Exception {
		File file = new File("src/test/resources/compilelistings/JOB26282.5");
		CompileListing compileListing = Parse.parse(file);
		Map<String, String> assignements = compileListing.getSourceMapAndCrossReference().getFileControlAssignment();
		assertThat(assignements, notNullValue());
		assertThat(assignements, hasEntry("NUM-LIST", "INPUT1"));
		assertThat(assignements, hasEntry("NUM-LIST-2", "INPUT2"));
		
		Map<String, String> fileSection = compileListing.getSourceMapAndCrossReference().getFileSection();
		assertThat(fileSection, notNullValue());
		assertThat(fileSection, hasEntry("NUM-LIST", "NUM-LIST-FIELDS"));
		assertThat(fileSection, hasEntry("NUM-LIST-2", "NUM-LIST-FIELDS-2"));
	}
	
	public static void testAssignmentNameParse( ) {
		String[][] ans = {
			{"PL-S-INPUT1", "INPUT1"}, 
			{"PL-AS-INPUT1", "INPUT1"},
			{"INPUT1", "INPUT1"},
			{"", ""},
			{null, null},
			{"-", "-"},
			{"PL-AS-INPUT1-LEGAL?", "AS-INPUT1-LEGAL?"}
		};
		Arrays.asList(ans).forEach(an -> {
			assertThat(Functions.parseAssignmentName(an[0]), equalTo(an[1]));
		});		
	}
	
	private void assertNotNullCompileListing(CompileListing compileListing) {
		assertNotNull(compileListing);
		assertNotNull(compileListing.getCompileOptions());
		assertNotNull(compileListing.getDataDivisionMap());
		assertNotNull(compileListing.getDataNamesCrossReference());
		assertNotNull(compileListing.getInvocationParameters());
		assertNotNull(compileListing.getOriginalSource());
		assertNotNull(compileListing.getProceduresCrossReference());
		assertNotNull(compileListing.getProgramsCrossReference());
		assertNotNull(compileListing.getSourceMapAndCrossReference());
	}
}
