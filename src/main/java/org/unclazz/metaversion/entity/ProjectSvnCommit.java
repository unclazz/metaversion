package org.unclazz.metaversion.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ProjectSvnCommit {
	private int projectId;
	private int commitId;
	public final int getProjectId() {
		return projectId;
	}
	public final void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public final int getCommitId() {
		return commitId;
	}
	public final void setCommitId(int commitId) {
		this.commitId = commitId;
	}
}
