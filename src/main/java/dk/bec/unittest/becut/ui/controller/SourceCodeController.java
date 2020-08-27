package dk.bec.unittest.becut.ui.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;

import dk.bec.unittest.becut.compilelist.CobolNodeType;
import dk.bec.unittest.becut.compilelist.TreeUtil;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import koopa.core.trees.Tree;

public class SourceCodeController {
	@FXML
	private WebView sourceView;

	private SimpleObjectProperty<List<String>> sourceProperty = new SimpleObjectProperty<>();
	
	public void initialize() {
		sourceProperty.bind(BECutAppContext.getContext().getSourceCode());
		WebEngine webEngine = sourceView.getEngine();
		sourceProperty.addListener((observable, oldValue, newValue) -> {
			webEngine.loadContent(html(newValue));
		});
		webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
			if (newState == State.SUCCEEDED) {
				Document doc = webEngine.getDocument();
				NodeList nl = doc.getElementsByTagName("div");
				if(nl != null) {
					for(int i = 0; i < nl.getLength(); i++) {
						Element n = (Element)nl.item(i);
						if("call".equals(n.getAttribute("class"))) {
							((EventTarget) n).addEventListener("click", ev -> {
								BECutAppContext.getContext().getEventBus().post(new ExternalCallLineEvent(Integer.parseInt(n.getAttribute("id"))));
							}, false);
						}
						
					}
				}
			}
		});
		BECutAppContext.getContext().getEventBus().register(SourceLineEvent.class, 
			event -> {
				webEngine.executeScript(
						String.format(
								"document.getElementById('%s').scrollIntoView({behavior: 'smooth', block: 'center'});", event.getLineNumber()));
				//{behavior: 'smooth', block: 'center'} should center the selected element within window; it does not seem to work so: 
				webEngine.executeScript("window.scrollBy(0, -200);");
		    });
	}

	String html(List<String> source) {
		//TODO use koopa ast to give (div) id separate classes to cobol reserved words, literals
		//TODO give id to line numbers
		//TODO use external stylesheet for coloring
		//TODO get rid of this telescope
		Tree ast = BECutAppContext.getContext().getUnitTestSuite().getCompileListing().getSourceMapAndCrossReference().getAst();
		//FIXME assertion: there is only one call per line
		Set<Integer> callSites = new HashSet<>();
		List<Tree> callStatements = TreeUtil.getDescendents(ast, CobolNodeType.CALL_STATEMENT);
		for (Tree callStatement : callStatements) {
			callSites.add(callStatement.getStartPosition().getLinenumber());
		}
		
		//C after line number is text inserted from copybook, let's skip it for clarity
		Pattern exclude = Pattern.compile(" {2}\\d{6}C\\s+.*");
		Pattern include = Pattern.compile(" {2}\\d{6}\\s+.*");
		String html = source
				.stream()
				.filter(line -> !exclude.matcher(line).matches())
				.filter(line -> include.matcher(line).matches())
				.map(line -> cutOutNoise(line))
				.map(line -> {
					int lineNumber = Integer.parseInt(line.substring(0, 6));
					if(callSites.contains(lineNumber)) {
						return "<div class=call id='" +  
								lineNumber + 
								"' style=\"background-color: #00FF00\"><a>" + line + "</a></div>";
					}
					return line;})
				.map(line -> line.matches("\\d{6}\\*.*")
						? "<div style=\"color: #408080\">" + line + "</div>"
						: line)
				.map(line -> "<p><pre>" + line + "</pre>")
				.collect(Collectors.joining("\n"));
		String content = "<html><body>" + html + "</body></html>";
		try {
			Files.write(Paths.get("/temp/source.html"), content.getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return content;
	}
	
	static String cutOutNoise(String s) {
		if(s == null || s.isEmpty()) return s;
		StringBuilder sb = new StringBuilder(80);
		for(int i = 0; i < s.length(); i++) {
			if(i < 2) continue; //first two spaces
			if(i > 7 && i < 23) continue; //space between line number and source text
			if(sb.length() == 80) break; //there can be no more than 80 chars
			sb.append(s.charAt(i));
		}
		return sb.toString();
	}
}
