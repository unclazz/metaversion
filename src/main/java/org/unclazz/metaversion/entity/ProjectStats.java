package org.unclazz.metaversion.entity;

import java.util.Date;

public class ProjectStats extends Project {
	private int commitCount;
	private Date minCommitDate;
	private Date maxCommitDate;
	private int pathCount;
	public final int getCommitCount() {
		return commitCount;
	}
	public final void setCommitCount(int commitCount) {
		this.commitCount = commitCount;
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
	public final int getPathCount() {
		return pathCount;
	}
	public final void setPathCount(int pathCount) {
		this.pathCount = pathCount;
	}
}
