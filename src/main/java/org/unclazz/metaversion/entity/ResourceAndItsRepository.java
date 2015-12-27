package org.unclazz.metaversion.entity;

public class ResourceAndItsRepository {
	private int id;
	private int repositoryId;
	private String path;
	private int repositoryName;
	private String repositoryBaseUrl;
	public final int getId() {
		return id;
	}
	public final void setId(int id) {
		this.id = id;
	}
	public final int getRepositoryId() {
		return repositoryId;
	}
	public final void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}
	public final String getPath() {
		return path;
	}
	public final void setPath(String path) {
		this.path = path;
	}
	public final int getRepositoryName() {
		return repositoryName;
	}
	public final void setRepositoryName(int repositoryName) {
		this.repositoryName = repositoryName;
	}
	public final String getRepositoryBaseUrl() {
		return repositoryBaseUrl;
	}
	public final void setRepositoryBaseUrl(String repositoryBaseUrl) {
		this.repositoryBaseUrl = repositoryBaseUrl;
	}
}
