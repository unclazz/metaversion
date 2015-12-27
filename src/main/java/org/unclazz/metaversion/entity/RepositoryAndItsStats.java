package org.unclazz.metaversion.entity;

import java.util.Date;

public class RepositoryAndItsStats extends Repository {
	private String lastCommitterId;
	private String lastCommitterName;
	private int lastCommitNumber;
	private Date lastCommitDate;
	private String lastCommitComment;
	private int resourceCount;
	public final String getLastCommitterId() {
		return lastCommitterId;
	}
	public final void setLastCommitterId(String lastCommitterId) {
		this.lastCommitterId = lastCommitterId;
	}
	public final String getLastCommitterName() {
		return lastCommitterName;
	}
	public final void setLastCommitterName(String lastCommitterName) {
		this.lastCommitterName = lastCommitterName;
	}
	public final int getLastCommitNumber() {
		return lastCommitNumber;
	}
	public final void setLastCommitNumber(int lastCommitNumber) {
		this.lastCommitNumber = lastCommitNumber;
	}
	public final Date getLastCommitDate() {
		return lastCommitDate;
	}
	public final void setLastCommitDate(Date lastCommitDate) {
		this.lastCommitDate = lastCommitDate;
	}
	public final String getLastCommitComment() {
		return lastCommitComment;
	}
	public final void setLastCommitComment(String lastCommitComment) {
		this.lastCommitComment = lastCommitComment;
	}
	public final int getResourceCount() {
		return resourceCount;
	}
	public final void setResourceCount(int resourceCount) {
		this.resourceCount = resourceCount;
	}
}
