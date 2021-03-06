package org.unclazz.metaversion.entity;

import java.util.Collections;
import java.util.List;

public class SvnCommitStats extends SvnCommit {
	private int pathCount;
	private int projectCount;
	private int projectId;
	private String projectName;
	private String projectCode;
	public int getBranchCount() {
		return branchCount;
	}
	public void setBranchCount(int btanchCount) {
		this.branchCount = btanchCount;
	}
	public List<String> getBranchNames() {
		return branchNames;
	}
	public void setBranchNames(List<String> branchNames) {
		this.branchNames = branchNames;
	}
	private int branchCount;
	private List<String> branchNames = Collections.emptyList();
	public final int getPathCount() {
		return pathCount;
	}
	public final void setPathCount(int pathCount) {
		this.pathCount = pathCount;
	}
	public final int getProjectCount() {
		return projectCount;
	}
	public final void setProjectCount(int projectCount) {
		this.projectCount = projectCount;
	}
	public final int getProjectId() {
		return projectId;
	}
	public final void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public final String getProjectName() {
		return projectName;
	}
	public final void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public final String getProjectCode() {
		return projectCode;
	}
	public final void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
}
