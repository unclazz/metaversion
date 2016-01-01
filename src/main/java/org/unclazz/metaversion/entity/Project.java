package org.unclazz.metaversion.entity;

public class Project {
	private int id;
	private String code;
	private String name;
	private String responsiblePerson;
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
	public final String getResponsiblePerson() {
		return responsiblePerson;
	}
	public final void setResponsiblePerson(String responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}
	public final String getCommitSignPattern() {
		return commitSignPattern;
	}
	public final void setCommitSignPattern(String commitSignPattern) {
		this.commitSignPattern = commitSignPattern;
	}
}
