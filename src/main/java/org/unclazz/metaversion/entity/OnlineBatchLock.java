package org.unclazz.metaversion.entity;

import java.util.Date;

public class OnlineBatchLock {
	private int id;
	private int programId;
	private boolean locked;
	private Date lastLockDate;
	private Date lastUnlockDate;
	public final int getId() {
		return id;
	}
	public final void setId(int id) {
		this.id = id;
	}
	public final int getProgramId() {
		return programId;
	}
	public final void setProgramId(int programId) {
		this.programId = programId;
	}
	public final boolean isLocked() {
		return locked;
	}
	public final void setLocked(boolean locked) {
		this.locked = locked;
	}
	public final Date getLastLockDate() {
		return lastLockDate;
	}
	public final void setLastLockDate(Date lastLockDate) {
		this.lastLockDate = lastLockDate;
	}
	public final Date getLastUnlockDate() {
		return lastUnlockDate;
	}
	public final void setLastUnlockDate(Date lastUnlockDate) {
		this.lastUnlockDate = lastUnlockDate;
	}
}
