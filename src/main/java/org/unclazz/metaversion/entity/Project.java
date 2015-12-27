package org.unclazz.metaversion.entity;

import java.util.Date;

public class Project {
	private int id;
	private String code;
	private String name;
	private String description;
	private String responsiblePerson;
	private Date startDate;
	private Date endDate;
	private boolean ended;
	private String commitSignPattern;
	public final int getId() {
		return id;
	}
	public final void setId(int id) {
		this.id = id;
	}
	public final String getCode() {
		return code;
	}
	public final void setCode(String code) {
		this.code = code;
	}
	public final String getName() {
		return name;
	}
	public final void setName(String name) {
		this.name = name;
	}
	public final String getDescription() {
		return description;
	}
	public final void setDescription(String description) {
		this.description = description;
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
	public final boolean isEnded() {
		return ended;
	}
	public final void setEnded(boolean ended) {
		this.ended = ended;
	}
	public final String getResponsiblePerson() {
		return responsiblePerson;
	}
	public final void setResponsiblePerson(String responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}
	public final String getCommitSignPattern() {
		return commitSignPattern;
	}
	public final void setCommitSignPattern(String commitSign) {
		this.commitSignPattern = commitSign;
	}
}
