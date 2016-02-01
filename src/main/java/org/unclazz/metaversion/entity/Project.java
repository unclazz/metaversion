package org.unclazz.metaversion.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Project {
	private int id;
	@JsonProperty(required=true)
	private String code;
	@JsonProperty(required=true)
	private String name;
	private String responsiblePerson;
	@JsonProperty(required=true)
	private String commitSignPattern;
	private boolean redoCommitLink;
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
	public boolean getRedoCommitLink() {
		return redoCommitLink;
	}
	public void setRedoCommitLink(boolean redoCommitLink) {
		this.redoCommitLink = redoCommitLink;
	}
}
