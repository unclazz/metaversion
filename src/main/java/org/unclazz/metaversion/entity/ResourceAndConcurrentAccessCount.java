package org.unclazz.metaversion.entity;

public class ResourceAndConcurrentAccessCount extends ResourceAndItsRepository {
	private int concurrentAccessCount;
	private int weakLockCount;
	public int getConcurrentAccessCount() {
		return concurrentAccessCount;
	}
	public void setConcurrentAccessCount(int concurrentAccessCount) {
		this.concurrentAccessCount = concurrentAccessCount;
	}
	public int getWeakLockCount() {
		return weakLockCount;
	}
	public void setWeakLockCount(int weakLockCount) {
		this.weakLockCount = weakLockCount;
	}
}
