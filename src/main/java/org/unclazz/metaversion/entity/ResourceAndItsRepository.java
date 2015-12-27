package org.unclazz.metaversion.entity;

public class ResourceAndItsRepository extends Resource {
	private int repositoryName;
	private String repositoryBaseUrl;
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
