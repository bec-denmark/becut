package dk.bec.unittest.becut.compilelist.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dk.bec.unittest.becut.compilelist.ParseExpandedSource;
import koopa.core.trees.Tree;
import koopa.core.trees.jaxen.Jaxen;

public class SourceMapAndCrossReference extends AbstractCompileListingSection {

	// Note that the ast will have the absolute line number
	private Tree ast;

	private Map<String, String> fileControlAssignments = new HashMap<>();

	public SourceMapAndCrossReference(List<String> source) {
		this.originalSource = source;
		ast = new ParseExpandedSource(source).createTree();

		// In following assignment: SELECT NUM-LIST ASSIGN TO INPUT1.
		// file name is returned from parser as tokens "INPUT" and "1"
		// it needs to be concatenated again.
		// Koopa error? Feature?
		List<?> fileControlEntries = Jaxen.evaluate(ast, "//fileControlParagraph//selectStatement");
		fileControlAssignments = fileControlEntries
				.stream()
				.map(Tree.class::cast)
				.map(tr -> {
			String key = Jaxen.evaluate(tr, "*//fileName//cobolWord//text()")
					.stream()
					.map(Tree.class::cast)
					.map(Tree::getText).collect(Collectors.joining());

			String value = Jaxen.evaluate(tr, "*//assignToClause//name//text()")
					.stream()
					.map(Tree.class::cast)
					.map(Tree::getText).collect(Collectors.joining());

			return new String[] { key, value };
		}).collect(Collectors.toMap(e -> e[0], e -> e[1]));
	}

	public Tree getAst() {
		return ast;
	}

	public Map<String, String> getFileControlAssignments() {
		return fileControlAssignments;
	}
}
