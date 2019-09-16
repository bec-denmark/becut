package dk.bec.unittest.becut.compilelist.model;


import java.util.List;

import dk.bec.unittest.becut.compilelist.ParseExpandedSource;
import koopa.core.trees.Tree;

public class SourceMapAndCrossReference extends AbstractCompileListingSection {
	
	//Note that the ast will have the absolute line number
	private Tree ast;
	
	public SourceMapAndCrossReference(List<String> source) {
		this.originalSource = source;
		ast = new ParseExpandedSource(source).createTree();
	}

	public Tree getAst() {
		return ast;
	}

}
