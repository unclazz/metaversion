package org.unclazz.metaversion.entity;

public class CommitResourceAndItsType extends CommitResource {
	private String modifiationTypeCode;
	private String modifiationTypeName;
	private String resourcePath;
	public final String getModifiationTypeCode() {
		return modifiationTypeCode;
	}
	public final void setModifiationTypeCode(String modifiationTypeCode) {
		this.modifiationTypeCode = modifiationTypeCode;
	}
	public final String getModifiationTypeName() {
		return modifiationTypeName;
	}
	public final void setModifiationTypeName(String modifiationTypeName) {
		this.modifiationTypeName = modifiationTypeName;
	}
	public final String getResourcePath() {
		return resourcePath;
	}
	public final void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}
}
