package dk.bec.unittest.becut.ftp;

public class FTPRetrieveFileException extends RuntimeException {
	private String ftpReply;
	
	public FTPRetrieveFileException(String ftpReply) {
		this.ftpReply = ftpReply;
	}
	
	@Override
	public String toString() {
		return "FTP reply:\n" + ftpReply;
	}
}
