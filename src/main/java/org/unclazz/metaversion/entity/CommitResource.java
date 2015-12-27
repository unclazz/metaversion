package org.unclazz.metaversion.entity;

public class CommitResource {
	private int commitId;
	private int resourceId;
	private int modifiationTypeId;
	private int repositoryId;
	public final int getCommitId() {
		return commitId;
	}
	public final void setCommitId(int commitId) {
		this.commitId = commitId;
	}
	public final int getResourceId() {
		return resourceId;
	}
	public final void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}
	public final int getModifiationTypeId() {
		return modifiationTypeId;
	}
	public final void setModifiationTypeId(int modifiationTypeId) {
		this.modifiationTypeId = modifiationTypeId;
	}
	public final int getRepositoryId() {
		return repositoryId;
	}
	public final void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}
}
