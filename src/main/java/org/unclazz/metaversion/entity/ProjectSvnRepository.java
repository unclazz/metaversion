package org.unclazz.metaversion.entity;

public class ProjectSvnRepository {
	private int projectId;
	private int svnRepositoryId;
	private int lastRevision;
	public final int getProjectId() {
		return projectId;
	}
	public final void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public final int getSvnRepositoryId() {
		return svnRepositoryId;
	}
	public final void setSvnRepositoryId(int svnRepositoryId) {
		this.svnRepositoryId = svnRepositoryId;
	}
	public final int getLastRevision() {
		return lastRevision;
	}
	public final void setLastRevision(int lastRevision) {
		this.lastRevision = lastRevision;
	}
}
