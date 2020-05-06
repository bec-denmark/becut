package dk.bec.unittest.becut.ui.controller;

import javafx.fxml.FXML;

public abstract class AbstractBECutDialogController extends AbstractBECutController {

	@FXML
	abstract protected void ok();

	@FXML
	protected void cancel() {
		closeWindow();
	}
}
