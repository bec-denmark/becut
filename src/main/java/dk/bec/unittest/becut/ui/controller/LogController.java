package dk.bec.unittest.becut.ui.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ResourceBundle;

import com.google.common.eventbus.Subscribe;

import dk.bec.unittest.becut.testcase.model.TestResult;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.HTMLEditor;

public class LogController implements Initializable {
	public static class ClearEvent {
	}
	
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
		BECutAppContext.getContext().getEventBus().register(new Object() {
		    @Subscribe
		    public void event(TestResult result) {
				switch (result.getStatus()) {
				case OK:
					appendText(htmlEditor, 
							"<div style=color:green>" + 
									result.getTestCase().getTestCaseName() + "<p>" + result.getMessage() +
							"</div>");
					break;
				case NOK:
					appendText(htmlEditor, 
							"<div style=color:red>" + 
									result.getTestCase().getTestCaseName() + "<p>" + result.getMessage() +
							"</div>");
					break;
				}
				appendText(htmlEditor, "<p>");
		    }
		});

		BECutAppContext.getContext().getEventBus().register(new Object() {
		    @Subscribe
		    public void event(LogController.ClearEvent clear) {
		    	htmlEditor.setHtmlText("");
		    }
		});
		
		Thread.setDefaultUncaughtExceptionHandler(
				(t, e) -> {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					appendText(htmlEditor, "<pre>" + sw.toString() + "</pre>");
					e.printStackTrace();
				});
	}

	private void appendText(HTMLEditor htmlEditor, String s) {
		htmlEditor.setHtmlText(htmlEditor.getHtmlText() + s);
	}
}
