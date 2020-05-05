package dk.bec.unittest.becut.ui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.recorder.RecorderManager;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import dk.bec.unittest.becut.ui.model.RuntimeEnvironment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class RecordProgramExecutionController extends AbstractBECutController implements Initializable {
	
	@FXML
	private TextField programName;
	
	@FXML
	private ComboBox<String> runtimeEnviromentsBox;
	
	@FXML 
	private TextField jobName;
	

	@FXML
	private Pane compileListingPane;
	
	private Node compileListingNode;
	
	private LoadCompileListingController compileListingController;
	

	@FXML
	private void cancel() {
		programName.getScene().getWindow().hide();
	}
	
	@FXML 
	protected void ok() {
		compileListingController.loadCompileListingIntoContext();
		CompileListing compileListing = BECutAppContext.getContext().getUnitTest().getCompileListing();
		BecutTestCase becutTestCase = RecorderManager.recordBatch(compileListing, programName.getText(), jobName.getText(), BECutAppContext.getContext().getCredential());
		BECutAppContext.getContext().getUnitTest().setBecutTestCase(becutTestCase);
		closeWindow();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		runtimeEnviromentsBox.setItems(RuntimeEnvironment.getRuntimeOptions());
		runtimeEnviromentsBox.getSelectionModel().selectFirst();
		FXMLLoader loader = new FXMLLoader();
		try {
			compileListingNode = loader.load(getClass().getResource("/dk/bec/unittest/becut/ui/view/LoadCompileListing.fxml").openStream());
			compileListingController = loader.getController();
			compileListingPane.getChildren().add(compileListingNode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected Stage getStage() {
		return (Stage)programName.getScene().getWindow();
	}
}
