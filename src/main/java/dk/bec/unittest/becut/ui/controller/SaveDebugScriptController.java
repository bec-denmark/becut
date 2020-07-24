package dk.bec.unittest.becut.ui.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import dk.bec.unittest.becut.compilelist.model.CompileListing;
import dk.bec.unittest.becut.debugscript.DebugScriptExecutor;
import dk.bec.unittest.becut.debugscript.ScriptGenerator;
import dk.bec.unittest.becut.ftp.model.Credential;
import dk.bec.unittest.becut.testcase.model.BecutTestCase;
import dk.bec.unittest.becut.ui.model.BECutAppContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SaveDebugScriptController {
	@FXML
	private TextField debugScriptPath;
	
	private File debugScriptFile;

	@FXML
	private void browse() {
		FileChooser chooser = new FileChooser();
		debugScriptFile = chooser.showSaveDialog(debugScriptPath.getScene().getWindow());
		if (debugScriptFile != null) {
			try {
				debugScriptPath.setText(debugScriptFile.getAbsolutePath());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	@FXML
	private void cancel() {
		debugScriptPath.getScene().getWindow().hide();
	}
	
	@FXML 
	void save() {
		debugScriptFile = new File(debugScriptPath.getText());
		BecutTestCase becutTestCase = BECutAppContext.getContext().getUnitTest().getBecutTestCase();
		CompileListing compileListing = BECutAppContext.getContext().getUnitTest().getCompileListing();
		if (compileListing == null || 
				!compileListing.getProgramName().toLowerCase().equals(becutTestCase.getProgramName().toLowerCase())) {
			Stage loadCompileListingStage = new Stage();
			loadCompileListingStage.initModality(Modality.WINDOW_MODAL);
			loadCompileListingStage.initOwner(debugScriptPath.getScene().getWindow());
			try {
				Parent parent = FXMLLoader.load(getClass().getResource("/dk/bec/unittest/becut/ui/view/LoadCompileListing.fxml"));
				Scene scene = new Scene(parent, 500, 200);
				loadCompileListingStage.setScene(scene);
			} catch (IOException e) {
				e.printStackTrace();
			} 
			loadCompileListingStage.showAndWait();
			return;
		}
		
		List<String> jcl = DebugScriptExecutor.createJCL(becutTestCase);
		
		try {
			Files.write(debugScriptFile.toPath(), jcl);
			becutTestCase.setDebugScriptPath(debugScriptFile.toPath());
		} catch (IOException e2) {
			throw new RuntimeException(e2);
		}

		debugScriptPath.getScene().getWindow().hide();
	}
}
