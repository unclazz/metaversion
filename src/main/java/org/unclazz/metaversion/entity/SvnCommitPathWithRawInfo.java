package org.unclazz.metaversion.entity;

public class SvnCommitPathWithRawInfo extends SvnCommitPath {
	private String rawPath;
	private String basePathSegment;
	private String branchPathSegment;
	public final String getRawPath() {
		return rawPath;
	}
	public final void setRawPath(String rawPath) {
		this.rawPath = rawPath;
	}
	public final String getBasePathSegment() {
		return basePathSegment;
	}
	public final void setBasePathSegment(String basePathSegment) {
		this.basePathSegment = basePathSegment;
	}
	public final String getBranchPathSegment() {
		return branchPathSegment;
	}
	public final void setBranchPathSegment(String branchPathSegment) {
		this.branchPathSegment = branchPathSegment;
	}
}
