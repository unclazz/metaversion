package org.unclazz.metaversion.entity;

import java.util.Date;

public class SvnCommit {
	private int id;
	private int svnRepositoryId;
	private int revision;
	private Date commitDate;
	private String comment;
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
	public final String getComment() {
		return comment;
	}
	public final void setComment(String comment) {
		this.comment = comment;
	}
	public final String getCommitterName() {
		return committerName;
	}
	public final void setCommitterName(String committerName) {
		this.committerName = committerName;
	}
}
