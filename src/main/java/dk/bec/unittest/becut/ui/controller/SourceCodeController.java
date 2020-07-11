package dk.bec.unittest.becut.ui.controller;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.EventTarget;

import dk.bec.unittest.becut.compilelist.CobolNodeType;
import dk.bec.unittest.becut.compilelist.TreeUtil;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import koopa.core.trees.Tree;

public class SourceCodeController {
	@FXML
	private WebView sourceView;

	private SimpleObjectProperty<List<String>> sourceProperty = new SimpleObjectProperty<>();
	
	public void initialize() {
		sourceProperty.bind(BECutAppContext.getContext().getSourceCode());
		sourceProperty.addListener((observable, oldValue, newValue) -> {
			String content = html(newValue);
			WebEngine webEngine = sourceView.getEngine();
			webEngine.loadContent(content);
			webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
				if (newState == State.SUCCEEDED) {
					Document doc = sourceView.getEngine().getDocument();
					Element el = doc.getElementById("aaa");
					((EventTarget) el).addEventListener("click", ev -> {
						System.out.println("BOOM!" + el);
						System.out.println(el.getAttribute("id"));
					}, false);
				}
			});
		});
	}

	String html(List<String> source) {
		//TODO get rid of this telescope
		Tree ast = BECutAppContext.getContext().getUnitTest().getCompileListing().getSourceMapAndCrossReference().getAst();
		//FIXME assertion: there is only one call per line
		Set<Integer> callSites = new HashSet<>();
		List<Tree> callStatements = TreeUtil.getDescendents(ast, CobolNodeType.CALL_STATEMENT);
		for (Tree callStatement : callStatements) {
			callSites.add(callStatement.getStartPosition().getLinenumber());
		}
		
		Pattern exclude = Pattern.compile(" {2}\\d{6}C\\s+\\d.*");
		Pattern include = Pattern.compile(" {2}\\d{6}\\s+\\d.*");
		String html = source
				.stream()
				.filter(line -> !exclude.matcher(line).matches())
				.filter(line -> include.matcher(line).matches())
				.map(line -> line.matches(" {2}\\d{6}\\s+.*") 
						&& callSites.contains(Integer.parseInt(line.substring(2, 8)))
						? "<div id='aaa' style=\"background-color: #00FF00\"><a>" + line + "</a></div>"
						: line)
				.map(line -> line.matches("\\d{6}\\s+\\*.*")
						? "<div style=\"background-color: #98FB98\">" + line + "</div>"
						: line)
				.map(line -> "<p><pre>" + line + "</pre>")
				.collect(Collectors.joining("\n"));
		String content = "<html><body>" + html + "</body></html>";
//		try {
//			Files.write(Paths.get("/temp/source.html"), content.getBytes());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return content;
	}
}
