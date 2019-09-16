package dk.bec.unittest.becut.test;

import koopa.core.data.Position;
import koopa.core.trees.Tree;

public class TestUtils {
	
	public TestUtils() {}

	public static void printAllNodeTypes(Tree tree) {
		if (tree.isNode()) {
			Position start = tree.getStartPosition();
			String startpos = start == null ? "" : start.toString();
			System.out.println("NODE:  " + tree.getName() + " - " + startpos);
		}
		for (Tree t: tree.getChildren()) {
			printAllNodeTypes(t);
		}
	}

}
