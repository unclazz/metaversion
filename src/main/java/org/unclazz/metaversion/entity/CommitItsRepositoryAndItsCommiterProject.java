package org.unclazz.metaversion.entity;

public class CommitItsRepositoryAndItsCommiterProject extends CommitAndItsCommiterProject {
	private String representativeRepositoryId;
	private String representativeRepositoryName;
	private int repositoryCount;
	public final String getRepresentativeRepositoryId() {
		return representativeRepositoryId;
	}
	public final void setRepresentativeRepositoryId(String representativeRepositoryId) {
		this.representativeRepositoryId = representativeRepositoryId;
	}
	public final String getRepresentativeRepositoryName() {
		return representativeRepositoryName;
	}
	public final void setRepresentativeRepositoryName(String representativeRepositoryName) {
		this.representativeRepositoryName = representativeRepositoryName;
	}
	public final int getRepositoryCount() {
		return repositoryCount;
	}
	public final void setRepositoryCount(int repositoryCount) {
		this.repositoryCount = repositoryCount;
	}
}
