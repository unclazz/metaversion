package org.unclazz.metaversion.entity;

public class WeakLockResource {
	private int projectId;
	private int resourceId;
	private int modifiationTypeId;
	public int getResourceId() {
		return resourceId;
	}
	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}
	public int getModifiationTypeId() {
		return modifiationTypeId;
	}
	public void setModifiationTypeId(int modifiationTypeId) {
		this.modifiationTypeId = modifiationTypeId;
	}
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
}
