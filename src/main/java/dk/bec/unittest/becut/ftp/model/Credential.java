package dk.bec.unittest.becut.ftp.model;

public class Credential {

	private String host;
	private String username;
	private String password;
	private Boolean cache;

	public Credential(String host, String username, String password, Boolean cache) {
		this.host = host;
		this.username = username;
		this.password = password;
		this.cache = cache;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getCache() {
		return cache;
	}

	public void setCache(Boolean cache) {
		this.cache = cache;
	}

}
