package org.unclazz.metaversion.entity;

public class SvnRepository {
	private int id;
	private String name;
	private String baseUrl;
	private String trunkPathPattern;
	private String branchPathPattern;
	private String username;
	private String password;
	private int maxRevision;
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
	public final String getTrunkPathPattern() {
		return trunkPathPattern;
	}
	public final void setTrunkPathPattern(String trunkPathPattern) {
		this.trunkPathPattern = trunkPathPattern;
	}
	public final String getBranchPathPattern() {
		return branchPathPattern;
	}
	public final void setBranchPathPattern(String branchPathPattern) {
		this.branchPathPattern = branchPathPattern;
	}
	public final String getUsername() {
		return username;
	}
	public final void setUsername(String username) {
		this.username = username;
	}
	public final String getPassword() {
		return password;
	}
	public final void setPassword(String password) {
		this.password = password;
	}
	public int getMaxRevision() {
		return maxRevision;
	}
	public void setMaxRevision(int maxRevision) {
		this.maxRevision = maxRevision;
	}
}
