package dk.bec.unittest.becut.ui.model;

import java.io.InputStream;

public interface LoadCompileListing {

	public InputStream getCompileListing();
	
	public void updateStatus();
}
