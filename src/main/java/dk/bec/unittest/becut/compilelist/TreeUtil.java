package dk.bec.unittest.becut.compilelist;

import java.util.ArrayList;
import java.util.List;

import koopa.core.trees.Tree;

public class TreeUtil {
	
	private TreeUtil() {}
	
	public static List<Tree> getDescendents(Tree tree, String nodeType) {
		return getDescendents(tree, nodeType, new ArrayList<Tree>());
	}

	private static List<Tree> getDescendents(Tree tree, String nodeType, List<Tree> descendents) {
		descendents.addAll(tree.getChildren(nodeType));
		for (Tree t : tree.getChildren()) {
			getDescendents(t, nodeType, descendents);
		}
		return descendents;
	}
	
	public static List<Tree> getDescendents(Tree tree, CobolNodeType nodeType) {
		return getDescendents(tree, nodeType.toString());
	}
	
	public static Tree getFirst(Tree tree, String nodeType) {
		if (tree == null) {
			return tree;
		}
		Tree child = tree.getChild(nodeType.toString());
		if (child == null) {
			for (Tree t : tree.getChildren()) {
				Tree c = getFirst(t, nodeType);
				if (c != null) {
					child = c;
					break;
				}
			}
		}
		return child;
	}

	public static String stripQuotes(String s) {
		return s.replaceAll("^['\"]*", "").replaceAll("['\"]*$", ""); 
	}
	


}
