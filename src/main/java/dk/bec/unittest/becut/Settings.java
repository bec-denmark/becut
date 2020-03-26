package dk.bec.unittest.becut;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class Settings {

	private Settings() {
	}

	private static final String PROPERTIES_FILENAME = "becut.properties";
	
	public static String FTP_HOST = "localhost";

	public static final int OUTPUTSTREAM_BUFFER_INITIAL_CAPACITY = 1000000;

	static {
		Properties properties = new Properties();
		// TODO lookup properties file based on machine name
		String hostname = getHostname();
		InputStream inputStream = Settings.class.getResourceAsStream("/" + hostname + "-" + PROPERTIES_FILENAME);
		if (inputStream == null) {
			inputStream = Settings.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
		}
		if (inputStream != null) {
			try {
				properties.load(inputStream);
				FTP_HOST = properties.getProperty("FTP_HOST");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if ("localhost".equals(FTP_HOST)) {
			System.out.println(
					"FTP host name not set. Communication with the mainframe will not work. Please add FTP_HOST to becut.properties");
		}
	}

	private static String getHostname() {
		String hostname = "Unknown";

		try {
			InetAddress addr;
			addr = InetAddress.getLocalHost();
			hostname = addr.getHostName();
		} catch (UnknownHostException ex) {
			System.out.println("Hostname can not be resolved");
		}
		return hostname;
	}

}
