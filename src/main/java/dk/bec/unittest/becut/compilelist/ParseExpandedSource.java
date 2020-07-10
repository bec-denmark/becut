package dk.bec.unittest.becut.compilelist;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
			if(line.contains("----+-*A-1-B--+----2----+----3----+----4----+----5----+----6----+----7-|--+----8 "))
				continue;
			int endOfLine = line.length() <= 98 ? line.length() : 98;
			if (line.length() > 17) {
				String substring = line.substring(17, endOfLine);
				if(substring.trim().length() > 0) {
					sb.append(substring);
					sb.append("\n");
				}
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
		//Files.write(Paths.get("/temp/rsi580.cbl"), fileContents.getBytes());
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
