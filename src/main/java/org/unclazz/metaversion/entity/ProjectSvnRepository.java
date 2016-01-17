package org.unclazz.metaversion.entity;

public class ProjectSvnRepository {
	private int projectId;
	private int repositoryId;
	private int lastRevision;
	public final int getProjectId() {
		return projectId;
	}
	public final void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public final int getRepositoryId() {
		return repositoryId;
	}
	public final void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}
	public final int getLastRevision() {
		return lastRevision;
	}
	public final void setLastRevision(int lastRevision) {
		this.lastRevision = lastRevision;
	}
}
