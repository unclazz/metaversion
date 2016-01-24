package org.unclazz.metaversion.entity;

import java.util.Date;

public class ProjectParallels {
	private int selfProjectId;
	private int repositoryId;
	private String repositoryName;
	private String path;
	private String parallelType;
	private int selfMinRevision;
	private Date selfMinCommitDate;
	private int selfMaxRevision;
	private Date selfMaxCommitDate;
	private int otherProjectId;
	private String otherProjectName;
	private String otherProjectCode;
	private String otherProjectResponsiblePerson;
	private int otherMinRevision;
	private Date otherMinCommitDate;
	private int otherMaxRevision;
	private Date otherMaxCommitDate;
	public final int getSelfProjectId() {
		return selfProjectId;
	}
	public final void setSelfProjectId(int selfProjectId) {
		this.selfProjectId = selfProjectId;
	}
	public final String getPath() {
		return path;
	}
	public final void setPath(String path) {
		this.path = path;
	}
	public final String getParallelType() {
		return parallelType;
	}
	public final void setParallelType(String parallelType) {
		this.parallelType = parallelType;
	}
	public final int getSelfMinRevision() {
		return selfMinRevision;
	}
	public final void setSelfMinRevision(int selfMinRevision) {
		this.selfMinRevision = selfMinRevision;
	}
	public final Date getSelfMinCommitDate() {
		return selfMinCommitDate;
	}
	public final void setSelfMinCommitDate(Date selfMinCommitDate) {
		this.selfMinCommitDate = selfMinCommitDate;
	}
	public final int getSelfMaxRevision() {
		return selfMaxRevision;
	}
	public final void setSelfMaxRevision(int selfMaxRevision) {
		this.selfMaxRevision = selfMaxRevision;
	}
	public final Date getSelfMaxCommitDate() {
		return selfMaxCommitDate;
	}
	public final void setSelfMaxCommitDate(Date selfMaxCommitDate) {
		this.selfMaxCommitDate = selfMaxCommitDate;
	}
	public final int getOtherProjectId() {
		return otherProjectId;
	}
	public final void setOtherProjectId(int otherProjectId) {
		this.otherProjectId = otherProjectId;
	}
	public final String getOtherProjectName() {
		return otherProjectName;
	}
	public final void setOtherProjectName(String otherProjectName) {
		this.otherProjectName = otherProjectName;
	}
	public final String getOtherProjectCode() {
		return otherProjectCode;
	}
	public final void setOtherProjectCode(String otherProjectCode) {
		this.otherProjectCode = otherProjectCode;
	}
	public final String getOtherProjectResponsiblePerson() {
		return otherProjectResponsiblePerson;
	}
	public final void setOtherProjectResponsiblePerson(String otherProjectResponsiblePerson) {
		this.otherProjectResponsiblePerson = otherProjectResponsiblePerson;
	}
	public final int getOtherMinRevision() {
		return otherMinRevision;
	}
	public final void setOtherMinRevision(int otherMinRevision) {
		this.otherMinRevision = otherMinRevision;
	}
	public final Date getOtherMinCommitDate() {
		return otherMinCommitDate;
	}
	public final void setOtherMinCommitDate(Date otherMinCommitDate) {
		this.otherMinCommitDate = otherMinCommitDate;
	}
	public final int getOtherMaxRevision() {
		return otherMaxRevision;
	}
	public final void setOtherMaxRevision(int otherMaxRevision) {
		this.otherMaxRevision = otherMaxRevision;
	}
	public final Date getOtherMaxCommitDate() {
		return otherMaxCommitDate;
	}
	public final void setOtherMaxCommitDate(Date otherMaxCommitDate) {
		this.otherMaxCommitDate = otherMaxCommitDate;
	}
	public final int getRepositoryId() {
		return repositoryId;
	}
	public final void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}
	public final String getRepositoryName() {
		return repositoryName;
	}
	public final void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}
}
