package org.unclazz.metaversion.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SvnCommit {
	private int id;
	private int svnRepositoryId;
	private int revision;
	@JsonFormat(pattern="yyyy/MM/dd HH:mm:ss.SSS")
	private Date commitDate;
	private String commitMessage;
	private String committerName;
	public final int getId() {
		return id;
	}
	public final void setId(int id) {
		this.id = id;
	}
	public final int getSvnRepositoryId() {
		return svnRepositoryId;
	}
	public final void setSvnRepositoryId(int repositoryId) {
		this.svnRepositoryId = repositoryId;
	}
	public final int getRevision() {
		return revision;
	}
	public final void setRevision(int revision) {
		this.revision = revision;
	}
	public final Date getCommitDate() {
		return commitDate;
	}
	public final void setCommitDate(Date commitDate) {
		this.commitDate = commitDate;
	}
	public final String getCommitMessage() {
		return commitMessage;
	}
	public final void setCommitMessage(String commitMessage) {
		this.commitMessage = commitMessage;
	}
	public final String getCommitterName() {
		return committerName;
	}
	public final void setCommitterName(String committerName) {
		this.committerName = committerName;
	}
}
