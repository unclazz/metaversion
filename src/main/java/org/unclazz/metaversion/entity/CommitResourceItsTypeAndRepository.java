package org.unclazz.metaversion.entity;

public class CommitResourceItsTypeAndRepository extends CommitResource {
	private int repositoryName;
	private int repositoryBaseUrl;
	public int getRepositoryName() {
		return repositoryName;
	}
	public void setRepositoryName(int repositoryName) {
		this.repositoryName = repositoryName;
	}
	public int getRepositoryBaseUrl() {
		return repositoryBaseUrl;
	}
	public void setRepositoryBaseUrl(int repositoryBaseUrl) {
		this.repositoryBaseUrl = repositoryBaseUrl;
	}
}
