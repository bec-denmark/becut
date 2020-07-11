package dk.bec.unittest.becut.ui.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import dk.bec.unittest.becut.ui.model.BECutAppContext;
import dk.bec.unittest.becut.ui.model.LoadCompileListing;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class LoadCompileListingFileController implements LoadCompileListing {

	@FXML
	private TextField compileListingPath;

	private File compileListingFile;

	@FXML
	private void browse() {

		FileChooser chooser = new FileChooser();
		compileListingFile = chooser.showOpenDialog(compileListingPath.getScene().getWindow());
		if (compileListingFile != null) {
			try {
				compileListingPath.setText(compileListingFile.getAbsolutePath());
			} catch (Exception e) {
				// handle exception...
			}
		}
	}

	@Override
	public InputStream getCompileListing() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			//TODO JESController and this both removes the first column: refactor to a common code
			//FIXME should be set by -Dfile.encoding=Cp1252 but somewhere it is set to UTF-8
			Files.readAllLines(Paths.get(compileListingPath.getText()), Charset.forName("Cp1252"))
				.stream()
				.forEach(line -> {
					byte[] bytes = line.getBytes();
					if(bytes.length > 1) {
						baos.write(bytes, 1, bytes.length - 1);
					} else {
						baos.write(bytes, 0, bytes.length);
					}
					baos.write('\n');
				});
			return new ByteArrayInputStream(baos.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void updateStatus() {
		BECutAppContext.getContext().getCompileListStatus().setValue("File: " + compileListingFile.getAbsolutePath());
	}

}
