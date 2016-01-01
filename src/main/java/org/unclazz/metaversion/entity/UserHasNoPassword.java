package org.unclazz.metaversion.entity;

public class UserHasNoPassword {
	private int id;
	private String name;
	private boolean admin;
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
	public final boolean isAdmin() {
		return admin;
	}
	public final void setAdmin(boolean admin) {
		this.admin = admin;
	}
}
