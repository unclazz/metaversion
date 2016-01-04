package org.unclazz.metaversion.entity;

public class SvnRepositoryStats {
	private int id;
	private String name;
	private String baseUrl;
	private int maxRevision;
	private int commitCount;
	private int pathCount;
	public final int getId() {
		return id;
	}
	public final void setId(int id) {
		this.id = id;
	}
	public final String getName() {
		return name;
	}
	public final void setName(String name) {
		this.name = name;
	}
	public final String getBaseUrl() {
		return baseUrl;
	}
	public final void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	public final int getMaxRevision() {
		return maxRevision;
	}
	public final void setMaxRevision(int maxRevision) {
		this.maxRevision = maxRevision;
	}
	public final int getCommitCount() {
		return commitCount;
	}
	public final void setCommitCount(int commitCount) {
		this.commitCount = commitCount;
	}
	public final int getPathCount() {
		return pathCount;
	}
	public final void setPathCount(int pathCount) {
		this.pathCount = pathCount;
	}
}
