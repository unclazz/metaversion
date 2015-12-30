package org.unclazz.metaversion.entity;

public class LogImportAndItsStatus extends LogImport {
	private String statusCode;
	private String repositoryName;
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getRepositoryName() {
		return repositoryName;
	}
	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}
}
