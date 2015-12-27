package org.unclazz.metaversion.entity;

public class CommitAndItsCommiterProject extends Commit {
	private String committerName;
	private int projectId;
	private String projectCode;
	private String projectName;
	public final String getCommitterName() {
		return committerName;
	}
	public final void setCommitterName(String committerName) {
		this.committerName = committerName;
	}
	public final int getProjectId() {
		return projectId;
	}
	public final void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public final String getProjectCode() {
		return projectCode;
	}
	public final void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	public final String getProjectName() {
		return projectName;
	}
	public final void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}
