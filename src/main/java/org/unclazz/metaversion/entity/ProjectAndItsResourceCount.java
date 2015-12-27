package org.unclazz.metaversion.entity;

public class ProjectAndItsResourceCount extends Project {
	private int weakLockedResourceCount;
	private int committedResourceCount;
	public int getWeakLockedResourceCount() {
		return weakLockedResourceCount;
	}
	public void setWeakLockedResourceCount(int weakLockedResourceCount) {
		this.weakLockedResourceCount = weakLockedResourceCount;
	}
	public int getCommittedResourceCount() {
		return committedResourceCount;
	}
	public void setCommittedResourceCount(int committedResourceCount) {
		this.committedResourceCount = committedResourceCount;
	}
}
