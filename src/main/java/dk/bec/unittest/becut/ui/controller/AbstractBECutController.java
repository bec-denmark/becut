package dk.bec.unittest.becut.ui.controller;

import dk.bec.unittest.becut.ui.model.BECutAppContext;
import javafx.stage.Stage;

public abstract class AbstractBECutController {
	
	abstract protected Stage getStage();

	public void closeWindow() {
		getStage().close();
	}
	
	public void closeApplication() {
		BECutAppContext.getContext().getPrimaryStage().close();
	}
	
}
