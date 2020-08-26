package dk.bec.unittest.becut.ui.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ResourceBundle;

import dk.bec.unittest.becut.ftp.FTPRetrieveFileException;
import dk.bec.unittest.becut.testcase.model.TestResult;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

public class LogController implements Initializable {
	@FXML
	private TextArea logArea;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		BECutAppContext.getContext().getTestResults().addListener((Change<? extends TestResult> c) -> {
			if(c.next() && c.wasAdded()) {
				c.getAddedSubList()
					.forEach(result -> {
						logArea.appendText(result.testCase.getTestCaseName());
						logArea.appendText(result.message);
						logArea.appendText("\n");
					});
			}
		});

		Thread.setDefaultUncaughtExceptionHandler(
			(t, e) -> {
				//TODO make this code readable
				if (e.getCause() instanceof InvocationTargetException
						&& ((InvocationTargetException)e.getCause()).getTargetException() 
							instanceof MissingINSPLOGException) {
					logArea.appendText(
							((InvocationTargetException)e.getCause())
							.getTargetException().toString() + "\n");
				} else if (e.getCause() instanceof InvocationTargetException
						&& ((InvocationTargetException)e.getCause()).getTargetException() 
							instanceof FTPRetrieveFileException) {
					logArea.appendText(
							((InvocationTargetException)e.getCause())
							.getTargetException().toString() + "\n");
				} else if (e.getCause() instanceof InvocationTargetException
						&& ((InvocationTargetException)e.getCause()).getTargetException() 
							instanceof LogMessage) {
					logArea.appendText(
							((InvocationTargetException)e.getCause())
							.getTargetException().toString() + "\n");
				} else if (e.getCause() instanceof InvocationTargetException
						&& ((InvocationTargetException)e.getCause()).getTargetException() 
							instanceof ReturnCodeDifferentFromCC000) {
					logArea.appendText(
							((InvocationTargetException)e.getCause())
							.getTargetException().toString() + "\n");
				} else {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					logArea.appendText(sw.toString() + "\n");
					e.printStackTrace();
				}
			}
		);
	}
}
