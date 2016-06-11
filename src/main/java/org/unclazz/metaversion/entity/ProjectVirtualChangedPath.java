package org.unclazz.metaversion.entity;

public class ProjectVirtualChangedPath {
	private int virtualChangedPathId;
	private int projectId;
	private int repositoryId;
	private String repositoryName;
	private int changeTypeId;
	private String path;
	public int getVirtualChangedPathId() {
		return virtualChangedPathId;
	}
	public void setVirtualChangedPathId(int virtualChangedPathId) {
		this.virtualChangedPathId = virtualChangedPathId;
	}
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public int getRepositoryId() {
		return repositoryId;
	}
	public void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}
	public String getRepositoryName() {
		return repositoryName;
	}
	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}
	public int getChangeTypeId() {
		return changeTypeId;
	}
	public void setChangeTypeId(int changeTypeId) {
		this.changeTypeId = changeTypeId;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
}
