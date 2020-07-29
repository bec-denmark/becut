package dk.bec.unittest.becut.ui.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import dk.bec.unittest.becut.ui.model.BECutAppContext;
import dk.bec.unittest.becut.ui.model.LoadCompileListing;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class LoadCompileListingFileController implements LoadCompileListing {

	@FXML
	private TextField compileListingPath;

	private File compileListingFile;

	static Path initialDirectory;
	
	@FXML
	private void browse() {
		FileChooser chooser = new FileChooser();
		if(initialDirectory != null && Files.exists(initialDirectory)) {
			chooser.setInitialDirectory(initialDirectory.toFile());
		}
		compileListingFile = chooser.showOpenDialog(compileListingPath.getScene().getWindow());
		if (compileListingFile != null) {
			try {
				initialDirectory = compileListingFile.toPath().getParent();
				compileListingPath.setText(compileListingFile.getAbsolutePath());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public InputStream getCompileListing() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			//FIXME should be set by -Dfile.encoding=Cp1252 but somewhere it is set to UTF-8
			Files.readAllLines(Paths.get(compileListingPath.getText()), Charset.forName("Cp1252"))
				.stream()
				.map(line -> line.replaceAll("\\s+$", ""))
				.forEach(line -> {
					byte[] bytes = line.getBytes();
					baos.write(bytes, 0, bytes.length);
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
