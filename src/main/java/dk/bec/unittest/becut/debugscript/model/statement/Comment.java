package dk.bec.unittest.becut.debugscript.model.statement;

import dk.bec.unittest.becut.debugscript.model.DebugEntity;

public class Comment extends StatementBase implements DebugEntity {

	private String commentText;

	public Comment(String commentText) {
		super();
		this.commentText = commentText;
	}

	public String getCommentText() {
		return commentText;
	}

	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}

	@Override
	public String generate() {
		return "      * " + commentText;
	}

}
