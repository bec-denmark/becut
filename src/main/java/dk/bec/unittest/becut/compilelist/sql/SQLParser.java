package dk.bec.unittest.becut.compilelist.sql;

import static koopa.core.grammars.combinators.Scoped.Visibility.PUBLIC;

import koopa.core.parsers.FutureParser;
import koopa.core.parsers.ParserCombinator;
import koopa.sql.grammar.SQLGrammar;

public class SQLParser {
	static class MySQLGrammar extends SQLGrammar {
		MySQLGrammar() {}
		
	    private ParserCombinator fetchParser = null;
	    
	    //TODO https://www.ibm.com/support/knowledgecenter/SSEPEK_11.0.0/sqlref/src/tpc/db2z_sql_fetch.html
	    public ParserCombinator fetchStatement() {
	      if (fetchParser == null) {
	        FutureParser future = scoped("fetch", PUBLIC, true);
	        fetchParser = future;
	        future.setParser(
	          sequence(
	            keyword("FETCH")
	,
	            identifier()
	,			
	            keyword("INTO")
	,
	            sequence(
	              selectStatement$into$target()
	,
	              star(
	                sequence(
	                  literal(",")
	,
	                  selectStatement$into$target()
	                )
	              )
	            )
	          )
	        );
	      }
	    
	      return fetchParser;
	    }
	};

	private static class FieldHolder {
	     static final MySQLGrammar g = new MySQLGrammar();
	}

	public static MySQLGrammar instanceOf() { return FieldHolder.g; }
}
