package org.unclazz.metaversion.entity;

public class Resource {
	private int id;
	private int repositoryId;
	private String path;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRepositoryId() {
		return repositoryId;
	}
	public void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
}
