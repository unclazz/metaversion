package org.unclazz.metaversion.entity;

import java.util.Date;

public class SystemBootLog {
	private int id;
	private Date bootDate;
	public final int getId() {
		return id;
	}
	public final void setId(int id) {
		this.id = id;
	}
	public final Date getBootDate() {
		return bootDate;
	}
	public final void setBootDate(Date bootDate) {
		this.bootDate = bootDate;
	}
}
