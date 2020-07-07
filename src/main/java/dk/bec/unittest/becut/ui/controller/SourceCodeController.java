package dk.bec.unittest.becut.ui.controller;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.EventTarget;

import dk.bec.unittest.becut.ui.model.BECutAppContext;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class SourceCodeController implements Initializable {
	@FXML
	private WebView sourceView;

	private static SimpleStringProperty sourceProperty = new SimpleStringProperty("sourceProperty");

	ChangeListener<? super String> listener;
		
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		sourceProperty.bind(BECutAppContext.getContext().getSourceCode());
		
		listener = (observable, oldValue, newValue) -> {
			System.out.println(newValue);
			String content = html(newValue);
			WebEngine webEngine = sourceView.getEngine();
			webEngine.loadContent(content);
			webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
				if(newState == State.SUCCEEDED) {
					Document doc = sourceView.getEngine().getDocument();
					Element el = doc.getElementById("aaa");
					((EventTarget) el).addEventListener("click", ev -> {
						System.out.println("BOOM!" + el);
						System.out.println(el.getAttribute("id"));
					}, false);
					
				}
			});
		};
		sourceProperty.addListener(listener);
	}

	String html(String source) {
		List<String> lines = Arrays.asList(source.split("\\r?\\n"));
		String html = lines.stream()
				.map(line -> "<p><pre>" + line + "</pre>")
				.map(line -> line.matches(".*CALL\\s+.*") 
						? "<div id='aaa' style=\"background-color: #00FF00\"><a>" + line + "</a></div>"
						: line)
				.map(line -> line.matches("\\d{6}\\s+\\*.*") 
						? "<div style=\"background-color: #98FB98\">" + line + "</div>"
						: line)
				.collect(Collectors.joining("\n"));
		String content = "<html><body>"
				+ html + "</body></html>";
		try {
			Files.write(Paths.get("/temp/source.html"), content.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
	}
	
}
