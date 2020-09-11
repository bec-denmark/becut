package dk.bec.unittest.becut.compilelist.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dk.bec.unittest.becut.compilelist.Functions;
import dk.bec.unittest.becut.compilelist.ParseExpandedSource;
import koopa.core.trees.Tree;
import koopa.core.trees.jaxen.Jaxen;

public class SourceMapAndCrossReference extends AbstractCompileListingSection {

	// Note that the ast will have the absolute line number
	private Tree ast;

	private Map<String, String> fileControlAssignment = new HashMap<>();
	private Map<String, String> fileSection = new HashMap<>();

	public SourceMapAndCrossReference(List<String> source) {
		this.originalSource = source;
		ast = new ParseExpandedSource(source).createTree();

		if(ast == null) {
			throw new AssertionError("Ast is null, there is a problem with the compile listing - wrong DDNAME/StepName?");
		}
		
		// In following assignment: SELECT NUM-LIST ASSIGN TO INPUT1.
		// file name is returned from parser as tokens "INPUT" and "1"
		// it needs to be concatenated again.
		// Koopa error? Feature?
		//
		// https://www.ibm.com/support/knowledgecenter/SSZHNR_1.0.0/com.ibm.ent.cbl.zos.doc/PGandLR/ref/rliosass.html
		// asssignement name may be like this: PL-S-INPUT1
		// it may be followed by 'S-' (for QSAM files, may be omitted), or 'AS-'
		fileControlAssignment = Jaxen.evaluate(ast, "//fileControlParagraph//selectStatement")
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

			return new String[] { key, Functions.parseAssignmentName(value) };
		}).collect(Collectors.toMap(e -> e[0], e -> e[1]));

		//koopa breaks file name NUM-LIST-2 into 'NUM-LIST-' and '2' elements,
		//it must be joined - JAXEN does not support XPath 2.0 which has string-join function
		//which might(?) help here
		fileSection = 
				Jaxen.evaluate(ast, "//dataDivision//fileSection//fileDescriptionEntry")
				.stream()
				.map(Tree.class::cast)
				.map(tr -> {
					String key = Jaxen.evaluate(tr, "*//fileName//text()")
							.stream()
							.map(Tree.class::cast)
							.map(Tree::getText)
							.collect(Collectors.joining());
					String value = Jaxen.evaluate(tr, 
							"following-sibling::recordDescriptionEntry[1]//entryName//text()")
					.stream()
					.map(Tree.class::cast)
					.map(Tree::getText)
					.collect(Collectors.joining());
					return new String[] {key, value};
				})
				.collect(Collectors.toMap(e -> e[0], e -> e[1]));
	}

	public Tree getAst() {
		return ast;
	}

	public Map<String, String> getFileControlAssignment() {
		return fileControlAssignment;
	}

	public Map<String, String> getFileSection() {
		return fileSection;
	}
}
