package org.unclazz.metaversion.entity;

import java.util.List;

public class SvnCommitWithRepositoryInfo extends SvnCommit {
	private String repositoryName;
	private String repositoryBaseUrl;
	private int branchCount;
	private List<String> branchNames;
	public int getBranchCount() {
		return branchCount;
	}
	public void setBranchCount(int branchCount) {
		this.branchCount = branchCount;
	}
	public List<String> getBranchNames() {
		return branchNames;
	}
	public void setBranchNames(List<String> branchName) {
		this.branchNames = branchName;
	}
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
