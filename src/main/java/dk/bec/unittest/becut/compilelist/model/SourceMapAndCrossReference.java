package dk.bec.unittest.becut.compilelist.model;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dk.bec.unittest.becut.compilelist.ParseExpandedSource;
import koopa.core.trees.Tree;
import koopa.core.trees.jaxen.Jaxen;

public class SourceMapAndCrossReference extends AbstractCompileListingSection {
	
	//Note that the ast will have the absolute line number
	private Tree ast;
	
	private List<String> fileControlAssignments = new ArrayList<>();
	
	public SourceMapAndCrossReference(List<String> source) {
		this.originalSource = source;
		ast = new ParseExpandedSource(source).createTree();

		//In following assignment: SELECT NUM-LIST ASSIGN TO INPUT1.
		//file name is returned from parser as tokens "INPUT" and "1"
		//it needs to be concatenated again.
		//Koopa error? Feature?
		List<?> fileControlEntries = Jaxen.evaluate(ast, 
					"//fileControlParagraph//selectStatement//assignClause");
		fileControlAssignments = fileControlEntries
				.stream()
				.map(e -> {
					Tree tr = (Tree)e;
					return Jaxen.evaluate(tr, "*//cobolWord//text()")
						.stream()
						.map(Tree.class::cast)
						.map(Tree::getText)
						.collect(Collectors.joining());
				})
				.collect(Collectors.toList());		
	}

	public Tree getAst() {
		return ast;
	}	

	public List<String> getFileControlAssignments() {
		return fileControlAssignments;
	}
}
