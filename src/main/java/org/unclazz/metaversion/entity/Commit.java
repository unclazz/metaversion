package org.unclazz.metaversion.entity;

import java.util.Date;

public class Commit {
	private int id;
	private int repositoryId;
	private int revisionNumber;
	private String comment;
	private int committerId;
	private Date commitDate;
	public final int getId() {
		return id;
	}
	public final void setId(int id) {
		this.id = id;
	}
	public final int getRepositoryId() {
		return repositoryId;
	}
	public final void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}
	public final int getRevisionNumber() {
		return revisionNumber;
	}
	public final void setRevisionNumber(int revisionNumber) {
		this.revisionNumber = revisionNumber;
	}
	public final int getCommitterId() {
		return committerId;
	}
	public final void setCommitterId(int committerId) {
		this.committerId = committerId;
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
}
