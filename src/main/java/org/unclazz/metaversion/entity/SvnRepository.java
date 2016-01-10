package org.unclazz.metaversion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SvnRepository {
	private static final String defaultTrunkPathPattern = "/trunk/";
	private static final String defaultBranchPathPattern = "/branches/\\w+";
	
	private int id = 0;
	@JsonProperty(required=true)
	private String name;
	@JsonProperty(required=true)
	private String baseUrl;
	private String trunkPathPattern = defaultTrunkPathPattern;
	private String branchPathPattern = defaultBranchPathPattern;
	private String username;
	private char[] password;
	private String encodedPassword;
	private int maxRevision = 0;
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
	@JsonIgnore
	public final String getEncodedPassword() {
		return encodedPassword;
	}
	public final void setEncodedPassword(String password) {
		this.encodedPassword = password;
	}
	public int getMaxRevision() {
		return maxRevision;
	}
	public void setMaxRevision(int maxRevision) {
		this.maxRevision = maxRevision;
	}
	@JsonIgnore
	public char[] getPassword() {
		return password;
	}
	@JsonProperty("password")
	public void setPassword(char[] password) {
		this.password = password;
	}
}
