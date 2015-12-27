package org.unclazz.metaversion.entity;

import java.util.Date;

public class ResourceItsRepositoryAndItsStats extends ResourceAndItsRepository {
	private String lastCommitComment;
	private Date lastCommitDate;
	private int lastCommitNumber;
	private int lastCommitterId;
	private String  lastCommitterName;
	private int concurrentAccessCount;
	private int weakLockCount;
	public final String getLastCommitComment() {
		return lastCommitComment;
	}
	public final void setLastCommitComment(String lastCommitComment) {
		this.lastCommitComment = lastCommitComment;
	}
	public final Date getLastCommitDate() {
		return lastCommitDate;
	}
	public final void setLastCommitDate(Date lastCommitDate) {
		this.lastCommitDate = lastCommitDate;
	}
	public final int getLastCommitNumber() {
		return lastCommitNumber;
	}
	public final void setLastCommitNumber(int lastCommitNumber) {
		this.lastCommitNumber = lastCommitNumber;
	}
	public final int getLastCommitterId() {
		return lastCommitterId;
	}
	public final void setLastCommitterId(int lastCommitterId) {
		this.lastCommitterId = lastCommitterId;
	}
	public final String getLastCommitterName() {
		return lastCommitterName;
	}
	public final void setLastCommitterName(String lastCommitterName) {
		this.lastCommitterName = lastCommitterName;
	}
	public final int getConcurrentAccessCount() {
		return concurrentAccessCount;
	}
	public final void setConcurrentAccessCount(int concurrentAccessCount) {
		this.concurrentAccessCount = concurrentAccessCount;
	}
	public final int getWeakLockCount() {
		return weakLockCount;
	}
	public final void setWeakLockCount(int weakLockCount) {
		this.weakLockCount = weakLockCount;
	}
}
