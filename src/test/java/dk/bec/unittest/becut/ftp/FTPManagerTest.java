package dk.bec.unittest.becut.ftp;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import dk.bec.unittest.becut.ftp.model.Credential;
import dk.bec.unittest.becut.ftp.model.JESFTPDataset;

public class FTPManagerTest {
	
	@Mock
	FTPClient ftpClient;
	
	@Mock
	Credential credential;
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Test
	public final void connectAndLoginTest() {
		when(credential.getHost()).thenReturn("host");
		when(credential.getUsername()).thenReturn("username");
		when(credential.getPassword()).thenReturn("password");
		when(ftpClient.getReplyString()).thenReturn("220").thenReturn("230");
		try {
			FTPManager.connectAndLogin(ftpClient, credential);
			verify(ftpClient, times(2)).getReplyString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public final void listJESTest() {
		JESFTPDataset[] jesftpDatasets = new JESFTPDataset[1];
		jesftpDatasets[0] = new JESFTPDataset();
		//JESFTPDataset[] spy = spy(jesftpDatasets);

		when(ftpClient.getReplyString()).thenReturn("250");
		String jobId = "JOB12345";
		try {
			when(ftpClient.listFiles(jobId)).thenReturn(jesftpDatasets);
			JESFTPDataset[] datasets = FTPManager.listJES(ftpClient, jobId);
			verify(ftpClient, times(1)).getReplyString();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	@Test
	public final void getJobTest() {
		
	}
	
	

}
