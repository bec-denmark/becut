package dk.bec.unittest.becut.compilelist.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.compilelist.model.DataNameReference;
import dk.bec.unittest.becut.compilelist.model.Record;
import koopa.core.data.Token;
import koopa.core.data.tags.AreaTag;
import koopa.core.trees.Tree;

public class SQLParse {
	
	private static Pattern intoPattern = Pattern.compile("^.*INTO((\\s|[\\\\rn])+((:[a-zA-Z0-9_-]+)((\\s|[\\\\rn])*,(\\s|[\\\\rn])*:[a-zA-Z0-9_-]+)*)).*$", Pattern.DOTALL);
	private static Pattern hostVariablePattern = Pattern.compile(":\\S+");
	
	public static List<Record> getHostVariables(String sql, CompileListing compileListing) throws Exception {
		//TODO This currently only works for simply queries and needs to be expanded to work in more situations
		List<Record> hostVariables = new ArrayList<>();
		List<String> hostVariableNames = getHostVariableNames(sql);
		for (String hostVariableName: hostVariableNames) {
			List<DataNameReference> dataNameReferences = compileListing.getDataNamesCrossReference().getDataNameReferencesByName().get(hostVariableName);
			if (dataNameReferences.size() == 1) {
				DataNameReference dataNameReference = dataNameReferences.get(0);
				Record record = compileListing.getDataDivisionMap().getRecord(dataNameReference.getLineNumber());
				hostVariables.add(record);
			}
			else {
				throw new Exception("More than one definition for host variable has been found because this search doesn't qualify variable names. Don't know what to do");
			}
		}
		return hostVariables;
	}
	
	public static List<String> getHostVariableNames(String sql) {
		List<String> variables = new ArrayList<>(); 
		if (hasReturnableHostVariables(sql)) {
			Matcher matchInto = intoPattern.matcher(sql);
			if (matchInto.matches()) {
				String intoClause = matchInto.group(1);
				Matcher matchHostVariables = hostVariablePattern.matcher(intoClause);
				while (matchHostVariables.find()) {
					variables.add(intoClause.substring(matchHostVariables.start() + 1, matchHostVariables.end()).replace(",", ""));
				}
				
			}
		}
		return variables;
	}
	
	public static Boolean hasReturnableHostVariables(String sql) {
		//this is super primitive
		String s = sql.toLowerCase();
		Boolean result = false;
		if (s.contains("select") || s.contains("fetch")) {
			if (s.contains("into")) {
				result = true;
			}
		}
		return result;
	}

	public static String findSQLStatement(Tree callStatement) {
		int currentLine = callStatement.getStartPosition().getLinenumber();
		Tree currentNode = callStatement;
		Map<Integer, List<Token>> commentBlocks = new HashMap<>();
		while (currentNode != null) {
			List<Token> tokens = currentNode.getTokens();
			List<Token> comments = tokens.stream().filter(t -> t.hasTag(AreaTag.COMMENT)).collect(Collectors.toList());
			List<Token> commentBlock = new ArrayList<>();
			boolean commentBlockStarted = false;
			for (Token comment: comments) {
				if (commentBlockStarted || comment.getText().toLowerCase().contains("exec sql")) {
					if (!commentBlockStarted) {
						commentBlock.add(comment);
						commentBlockStarted = true;
					}
					else {
						if (commentBlock.get(commentBlock.size()-1).getStart().getLinenumber() == comment.getStart().getLinenumber() - 1) {
							commentBlock.add(comment);
						}
						else {
							commentBlocks.put(commentBlock.get(0).getStart().getLinenumber(), commentBlock);
							//Since this is the precompiler this code should be placed two line (unless there is a change in the precompiler)
							if (commentBlock.get(commentBlock.size()-1).getStart().getLinenumber() + 2 == currentLine) {
								return blockToText(commentBlock);
							}
							commentBlockStarted = false;
							commentBlock = new ArrayList<Token>();
							if (comment.getText().toLowerCase().contains("exec sql")) {
								commentBlock.add(comment);
								commentBlockStarted = true;
							}
						}
					}
				}
				if (commentBlock.size() > 0) {
					commentBlocks.put(commentBlock.get(0).getStart().getLinenumber(), commentBlock);
					//Since this is the precompiler this code should be placed two line (unless there is a change in the precompiler)
					if (commentBlock.get(commentBlock.size()-1).getStart().getLinenumber() + 2 == currentLine) {
						return blockToText(commentBlock);
					}
				}
			}
			currentNode = currentNode.getParent();
		}

		//We are in an unknown situation and try to find something 
		int reverseSearch = currentLine;
		while (reverseSearch > 0) {
			List<Token> commentBlock = commentBlocks.get(reverseSearch);
			if (commentBlock != null) {
				return blockToText(commentBlock);
			}
			reverseSearch--;
		}
		return null;
	}
	
	private static String blockToText(List<Token> lines) {
		String acc = "";
		for (Token line: lines) {
			acc = acc + line.getText().replaceAll("^\\*+", "") + "\n";
		}
		return acc;
	}
	

}
