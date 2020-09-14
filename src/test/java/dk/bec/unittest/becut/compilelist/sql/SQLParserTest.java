package dk.bec.unittest.becut.compilelist.sql;

import static org.junit.Assert.assertTrue;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import dk.bec.unittest.becut.compilelist.sql.SQLParser.MySQLGrammar;
import koopa.core.parsers.Parse;
import koopa.core.parsers.ParserCombinator;
import koopa.core.sources.BasicTokens;
import koopa.core.sources.Source;
import koopa.core.trees.KoopaTreeBuilder;
import koopa.core.trees.Tree;
import koopa.core.trees.jaxen.Jaxen;

public class SQLParserTest {
	static String fetch1 = "FETCH C1 INTO :DNUM, :DNAME, :MNUM";
	static String fetch2 = "FETCH ROWSET STARTING AT ABSOLUTE -5 \r\n" + 
			"   FROM C1 FOR 5 ROWS INTO DESCRIPTOR :MYDESCR";
	static String fetch3 = "FETCH ROWSET STARTING AT ABSOLUTE 10\r\n" + 
			"   FROM CURS1 FOR 6 ROWS\r\n" + 
			"   INTO :hav1, :hva2, :hva3";
	static String fetch4 = "FETCH ROWSET STARTING AT ABSOLUTE 10\r\n" + 
			"   FROM CURS1 FOR 6 ROWS\r\n" + 
			"   INTO DESCRIPTOR :MYDESCR"; 
	
	@Test
	public void shouldRecognizeSimpleFetch() {
		MySQLGrammar g = SQLParser.instanceOf();
		
		final Reader reader = new StringReader(fetch1);
		Source source = BasicTokens.getNewSource("test", reader);
		final ParserCombinator identifier = g.fetchStatement();
		final Parse parse = Parse.of(source).to(new KoopaTreeBuilder(g));
		final boolean accepts = identifier.accepts(parse);
		assertTrue(accepts);

//		display parse result in a window
//		final KoopaTreeBuilder builder = parse
//				.getTarget(KoopaTreeBuilder.class);
//		List<Tree> asts = builder.getTrees();
//		for (Tree ast : asts)
//			new TreeFrame(fetch1, ast).setVisible(true);		
	}

	@Test
	public void shouldListHostVariables() {
		MySQLGrammar g = SQLParser.instanceOf();
		
		final Reader reader = new StringReader("FETCH C1 INTO :DNUM, :DNAME, :MNUM");
		Source source = BasicTokens.getNewSource("test", reader);
		final ParserCombinator identifier = g.fetchStatement();
		final Parse parse = Parse.of(source).to(new KoopaTreeBuilder(g));
		final boolean accepts = identifier.accepts(parse);
		assertTrue(accepts);

		final KoopaTreeBuilder builder = parse.getTarget(KoopaTreeBuilder.class);
		List<Tree> trees = builder.getTrees();
		
		assertTrue(trees.size() == 1);
		
		List<String> hostParameters  = Jaxen.evaluate(trees.get(0), "*//hostParameterName")
				.stream()
				.map(Tree.class::cast)
				.map(t -> {
					String name = Jaxen.evaluate(t, "text()|*//text()")
							.stream()
							.map(Tree.class::cast)
							.map(Tree::getText)
							.collect(Collectors.joining());					
					return name;
				})
				.collect(Collectors.toList());
		
		assertTrue(hostParameters.size() == 3);
		assertTrue(new HashSet<>(hostParameters).containsAll(Arrays.asList(":DNUM", ":DNAME", ":MNUM")));
	}
	
//	public static void main(String[] args) {
//		MySQLGrammar g = SQLParser.instanceOf();
//		
//		final Reader reader = new StringReader(fetch1);
//		Source source = BasicTokens.getNewSource("test", reader);
//		final ParserCombinator identifier = g.fetchStatement();
//		final Parse parse = Parse.of(source).to(new KoopaTreeBuilder(g));
//		
//		assert identifier.accepts(parse);
//
//		final KoopaTreeBuilder builder = parse
//				.getTarget(KoopaTreeBuilder.class);
//		List<Tree> asts = builder.getTrees();
//		for (Tree ast : asts)
//			new TreeFrame(fetch1, ast).setVisible(true);		
//	}
}
