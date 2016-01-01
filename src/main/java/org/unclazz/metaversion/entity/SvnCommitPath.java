package org.unclazz.metaversion.entity;

public class SvnCommitPath {
	private int id;
	private int svnCommitId;
	private int changeTypeId;
	private String path;
	public final int getId() {
		return id;
	}
	public final void setId(int id) {
		this.id = id;
	}
	public final int getSvnCommitId() {
		return svnCommitId;
	}
	public final void setSvnCommitId(int svnCommitId) {
		this.svnCommitId = svnCommitId;
	}
	public final int getChangeTypeId() {
		return changeTypeId;
	}
	public final void setChangeTypeId(int changeTypeId) {
		this.changeTypeId = changeTypeId;
	}
	public final String getPath() {
		return path;
	}
	public final void setPath(String path) {
		this.path = path;
	}
}
