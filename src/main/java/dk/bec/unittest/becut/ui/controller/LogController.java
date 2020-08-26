package dk.bec.unittest.becut.ui.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ResourceBundle;

import dk.bec.unittest.becut.ftp.FTPRetrieveFileException;
import dk.bec.unittest.becut.testcase.model.TestResult;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import javafx.application.Platform;
import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.HTMLEditor;

public class LogController implements Initializable {
	@FXML
	private BorderPane pane;










	@Override
	public void initialize(URL location, ResourceBundle resources) {
		HTMLEditor htmlEditor = new HTMLEditor();
		//https://stackoverflow.com/a/25388790
		htmlEditor.setVisible(false);
		Platform.runLater(() -> {
			htmlEditor.lookupAll(".tool-bar")
					.forEach(n -> {
						n.setVisible(false);
						n.setManaged(false);
					});
			htmlEditor.setVisible(true);
		});

		pane.setCenter(htmlEditor);
		BECutAppContext.getContext().getTestResults().addListener((Change<? extends TestResult> c) -> {
			if (c.next() && c.wasAdded()) {
				c.getAddedSubList()
						.forEach(result -> {
							switch (result.status) {
							case OK:
								appendText(htmlEditor, 
										"<div style=color:green>" + 
												result.testCase.getTestCaseName() + "<p>" + result.message +
										"</div>");
								break;
							case NOK:
								appendText(htmlEditor, 
										"<div style=color:red>" + 
												result.testCase.getTestCaseName() + "<p>" + result.message +
										"</div>");
								break;
							}
							appendText(htmlEditor, "<p>");
						});
			}
		});

		Thread.setDefaultUncaughtExceptionHandler(
				(t, e) -> {
					//TODO make this code readable
					if (e.getCause() instanceof InvocationTargetException
							&& ((InvocationTargetException) e.getCause())
									.getTargetException() instanceof MissingINSPLOGException) {
						appendText(htmlEditor,
								((InvocationTargetException) e.getCause())
										.getTargetException().toString() + "<p>");
					} else if (e.getCause() instanceof InvocationTargetException
							&& ((InvocationTargetException) e.getCause())
									.getTargetException() instanceof FTPRetrieveFileException) {
						appendText(htmlEditor,
								((InvocationTargetException) e.getCause())
										.getTargetException().toString() + "<p>");
					} else if (e.getCause() instanceof InvocationTargetException
							&& ((InvocationTargetException) e.getCause()).getTargetException() instanceof LogMessage) {
						appendText(htmlEditor,
								((InvocationTargetException) e.getCause())
										.getTargetException().toString() + "<p>");
					} else if (e.getCause() instanceof InvocationTargetException
							&& ((InvocationTargetException) e.getCause())
									.getTargetException() instanceof ReturnCodeDifferentFromCC000) {
						appendText(htmlEditor,
								((InvocationTargetException) e.getCause())
										.getTargetException().toString() + "<p>");
					} else {
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						e.printStackTrace(pw);
						appendText(htmlEditor, sw.toString() + "<p>");
						e.printStackTrace();
					}
				});
	}

	private void appendText(HTMLEditor htmlEditor, String s) {
		htmlEditor.setHtmlText(htmlEditor.getHtmlText() + s);
	}
}
