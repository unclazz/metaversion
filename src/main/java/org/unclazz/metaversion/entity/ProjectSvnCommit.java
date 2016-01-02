package org.unclazz.metaversion.entity;

public class ProjectSvnCommit {
	private int projectId;
	private int svnCommitId;
	public final int getProjectId() {
		return projectId;
	}
	public final void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public final int getSvnCommitId() {
		return svnCommitId;
	}
	public final void setSvnCommitId(int svnCommitId) {
		this.svnCommitId = svnCommitId;
	}
}
