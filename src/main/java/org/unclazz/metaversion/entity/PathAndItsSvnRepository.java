package org.unclazz.metaversion.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class PathAndItsSvnRepository {
	private String path;
	private int svnRepositoryId;
	private String svnRepositoryName;
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
	public final int getSvnRepositoryId() {
		return svnRepositoryId;
	}
	public final void setSvnRepositoryId(int repositoryId) {
		this.svnRepositoryId = repositoryId;
	}
	public final String getSvnRepositoryName() {
		return svnRepositoryName;
	}
	public final void setSvnRepositoryName(String repositoryName) {
		this.svnRepositoryName = repositoryName;
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
