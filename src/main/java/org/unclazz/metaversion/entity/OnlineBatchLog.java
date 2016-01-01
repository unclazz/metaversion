package org.unclazz.metaversion.entity;

import java.util.Date;

public class OnlineBatchLog {
	private int id;
	private Date startDate;
	private Date endDate;
	private int statusId;
	private int programId;
	public final int getId() {
		return id;
	}
	public final void setId(int id) {
		this.id = id;
	}
	public final Date getStartDate() {
		return startDate;
	}
	public final void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public final Date getEndDate() {
		return endDate;
	}
	public final void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public final int getStatusId() {
		return statusId;
	}
	public final void setStatusId(int statusId) {
		this.statusId = statusId;
	}
	public final int getProgramId() {
		return programId;
	}
	public final void setProgramId(int programId) {
		this.programId = programId;
	}
}
