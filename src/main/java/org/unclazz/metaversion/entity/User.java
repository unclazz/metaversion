package org.unclazz.metaversion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class User {
	
	private int id;
	@JsonProperty(required=true)
	private String name;
	private String encodedPassword;
	private char[] password;
	private boolean admin;
	
	public boolean isAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@JsonIgnore
	public String getEncodedPassword() {
		return encodedPassword;
	}
	public void setEncodedPassword(String password) {
		this.encodedPassword = password;
	}
	@JsonIgnore
	public char[] getPassword() {
		return password;
	}
	@JsonProperty("password")
	public void setPassword(char[] rawPassword) {
		this.password = rawPassword;
	}
}
