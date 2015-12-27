package org.unclazz.metaversion.entity;

public class Repository {
	private int id;
	private String name;
	private String baseUrl;
	private String svnUsername;
	private String svnPassword;
	public final int getId() {
		return id;
	}
	public final void setId(int id) {
		this.id = id;
	}
	public final String getName() {
		return name;
	}
	public final void setName(String name) {
		this.name = name;
	}
	public final String getBaseUrl() {
		return baseUrl;
	}
	public final void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	public final String getSvnUsername() {
		return svnUsername;
	}
	public final void setSvnUsername(String svnUsername) {
		this.svnUsername = svnUsername;
	}
	public final String getSvnPassword() {
		return svnPassword;
	}
	public final void setSvnPassword(String svnPassword) {
		this.svnPassword = svnPassword;
	}
}
