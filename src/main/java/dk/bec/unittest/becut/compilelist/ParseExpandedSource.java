package dk.bec.unittest.becut.compilelist;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import koopa.app.CobolParserFactory;
import koopa.cobol.parser.ParseResults;
import koopa.cobol.projects.StandardCobolProject;
import koopa.cobol.sources.SourceFormat;
import koopa.core.trees.KoopaTreeBuilder;
import koopa.core.trees.Tree;

public class ParseExpandedSource {

	private List<String> source;

	public ParseExpandedSource(List<String> source) {
		this.source = source;
	}

	private String cleanCompileListing(List<String> expandedSource) {
		StringBuilder sb = new StringBuilder(expandedSource.size());
		for (String line : expandedSource) {
			int endOfLine = line.length() <= 98 ? line.length() : 98;
			if (line.length() > 17) {
				sb.append(line.substring(17, endOfLine) + "\n");
			} 
			else {
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	public static File createTempFile(String fileContents) throws IOException {
		File file = File.createTempFile("cobol", "parse");
		FileWriter fw = new FileWriter(file, false);
		fw.write(fileContents);
		fw.close();
		return file;
	}

	public Tree createTree() {
		String cleanSource = cleanCompileListing(source);
		Tree tree = null;
		File file = null;
		try {
			file = createTempFile(cleanSource);
			StandardCobolProject cobolProject = new StandardCobolProject();
			cobolProject.setDefaultFormat(SourceFormat.FIXED);
			CobolParserFactory cobolParserFactory = new CobolParserFactory(cobolProject);
			cobolParserFactory.setKeepingTrackOfTokens(true);
			cobolParserFactory.setBuildTrees(true);
			ParseResults results = cobolParserFactory.getParser().parse(file);
			tree = results.getParse().getTarget(KoopaTreeBuilder.class).getTree();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (file.exists()) {
				file.delete();
			}
		}
		return tree;

	}

}
