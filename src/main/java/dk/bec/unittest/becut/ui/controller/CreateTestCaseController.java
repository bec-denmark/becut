package dk.bec.unittest.becut.ui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.testcase.BecutTestCaseManager;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class CreateTestCaseController extends AbstractBECutController implements Initializable {
	
	@FXML
	private Pane compileListingPane;
	
	private Node compileListingNode;
	
	private LoadCompileListingController compileListingController;
	
	@FXML
	private TextField testCaseName;
	
	@FXML
	private TextField testCaseID;
	
	@FXML
	private void cancel() {
		closeWindow();
	}
	
	@FXML 
	void ok() {
		compileListingController.loadCompileListingIntoContext();

		CompileListing compileListing = BECutAppContext.getContext().getUnitTest().getCompileListing();
		BecutTestCase becutTestCase = BecutTestCaseManager.createTestCaseFromCompileListing(compileListing);
		if (!testCaseName.getText().trim().isEmpty()) {
			becutTestCase.setTestCaseName(testCaseName.getText());
		}
		if (!testCaseID.getText().trim().isEmpty()) {
			becutTestCase.setTestCaseId(testCaseID.getText());
		}

		BECutAppContext.getContext().getUnitTest().setBecutTestCase(becutTestCase);

		closeWindow();
	}

	@Override
	protected Stage getStage() {
		return (Stage) testCaseName.getScene().getWindow();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
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
}
