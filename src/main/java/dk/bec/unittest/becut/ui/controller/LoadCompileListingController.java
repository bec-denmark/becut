package dk.bec.unittest.becut.ui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import dk.bec.unittest.becut.compilelist.Parse;
import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import dk.bec.unittest.becut.ui.model.LoadCompileListing;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

public class LoadCompileListingController implements Initializable {

	@FXML
	private ComboBox<CompileListType> compileListingMethod;

	@FXML
	private Pane content;

	private LoadCompileListing currentCompileListing;

	@FXML
	private LoadCompileListingFileController loadCompileListingFileController;
	
	private Map<CompileListType, Pair<Node, LoadCompileListing>> compileListingSource = new HashMap<CompileListType, Pair<Node,LoadCompileListing>>();

	@FXML
	private Button ok;
	@FXML
	public void ok() {
		loadCompileListingIntoContext();
	}

	public LoadCompileListing getCurrentCompileListing() {
		return currentCompileListing;
	}
	
	public void loadCompileListingIntoContext() {
		currentCompileListing.updateStatus();
		CompileListing compileListing = Parse.parse(currentCompileListing.getCompileListing());
		BECutAppContext.getContext().getUnitTest().setCompileListing(compileListing);
		List<String> lines = BECutAppContext.getContext().getUnitTest()
				.getCompileListing().getSourceMapAndCrossReference().getOriginalSource();
		BECutAppContext.getContext().getSourceCode().setValue(lines);
		ok.getScene().getWindow().hide();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Setup combobox
		compileListingMethod.getItems().addAll(CompileListType.values());
		compileListingMethod.valueProperty().addListener((obs, oldChoice, newChoice) -> {
			content.getChildren().clear();
			Pair<Node, LoadCompileListing> currentType = compileListingSource.get(newChoice);
			content.getChildren().add(currentType.getKey());
			currentCompileListing = currentType.getValue();
		});
		
		//Setup all subnodes
		try {
			//compilingListFile = FXMLLoader.load(getClass().getResource("/dk/bec/unittest/becut/ui/view/LoadCompileListingFile.fxml"));
			FXMLLoader datasetLoader =  new FXMLLoader();
			Node compilingListDataset = datasetLoader.load(getClass().getResource("/dk/bec/unittest/becut/ui/view/LoadCompileListingDataset.fxml").openStream());
			compileListingSource.put(CompileListType.DATASET, new Pair<Node, LoadCompileListing>(compilingListDataset, datasetLoader.getController()));

			FXMLLoader fileLoader =  new FXMLLoader();
			Node compilingListFile = fileLoader.load(getClass().getResource("/dk/bec/unittest/becut/ui/view/LoadCompileListingFile.fxml").openStream());
			compileListingSource.put(CompileListType.FILE, new Pair<Node, LoadCompileListing>(compilingListFile, fileLoader.getController()));

			FXMLLoader JESLoader =  new FXMLLoader();
			Node compilingListJES = JESLoader.load(getClass().getResource("/dk/bec/unittest/becut/ui/view/LoadCompileListingJES.fxml").openStream());
			compileListingSource.put(CompileListType.JES, new Pair<Node, LoadCompileListing>(compilingListJES, JESLoader.getController()));

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		compileListingMethod.getSelectionModel().selectFirst();
	}

	private enum CompileListType {
		FILE("File"),
		DATASET("Dataset"),
		JES("JES")
		;
		
		private String guiName;
		
		private CompileListType(String guiName) {
			this.guiName = guiName;
		}
		
		@Override
		public String toString() {
			return guiName;
		}
	}

}
