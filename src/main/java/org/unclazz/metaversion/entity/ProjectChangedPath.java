package org.unclazz.metaversion.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ProjectChangedPath {
	private String path;
	private int repositoryId;
	private String repositoryName;
	private int commitCount;
	private int minRevision;
	private int maxRevision;
	@JsonFormat(pattern="yyyy/MM/dd HH:mm:ss.SSS")
	private Date minCommitDate;
	@JsonFormat(pattern="yyyy/MM/dd HH:mm:ss.SSS")
	private Date maxCommitDate;
	public final String getPath() {
		return path;
	}
	public final void setPath(String path) {
		this.path = path;
	}
	public final int getRepositoryId() {
		return repositoryId;
	}
	public final void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}
	public final String getRepositoryName() {
		return repositoryName;
	}
	public final void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}
	public final int getCommitCount() {
		return commitCount;
	}
	public final void setCommitCount(int commitCount) {
		this.commitCount = commitCount;
	}
	public final int getMinRevision() {
		return minRevision;
	}
	public final void setMinRevision(int minRevision) {
		this.minRevision = minRevision;
	}
	public final int getMaxRevision() {
		return maxRevision;
	}
	public final void setMaxRevision(int maxRevision) {
		this.maxRevision = maxRevision;
	}
	public final Date getMinCommitDate() {
		return minCommitDate;
	}
	public final void setMinCommitDate(Date minCommitDate) {
		this.minCommitDate = minCommitDate;
	}
	public final Date getMaxCommitDate() {
		return maxCommitDate;
	}
	public final void setMaxCommitDate(Date maxCommitDate) {
		this.maxCommitDate = maxCommitDate;
	}
}
