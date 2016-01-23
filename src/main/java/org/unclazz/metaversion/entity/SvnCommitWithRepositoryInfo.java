package org.unclazz.metaversion.entity;

public class SvnCommitWithRepositoryInfo extends SvnCommit {
	private String repositoryName;
	private String repositoryBaseUrl;
	public final String getRepositoryName() {
		return repositoryName;
	}
	public final void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}
	public final String getRepositoryBaseUrl() {
		return repositoryBaseUrl;
	}
	public final void setRepositoryBaseUrl(String repositoryBaseUrl) {
		this.repositoryBaseUrl = repositoryBaseUrl;
	}
}
