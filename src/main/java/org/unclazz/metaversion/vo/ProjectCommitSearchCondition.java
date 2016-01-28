package org.unclazz.metaversion.vo;

public class ProjectCommitSearchCondition {
	private int projectId = 0;
	private boolean unlinked = false;
	private boolean pathbase = false;
	private String like = "";
	private final Paging paging = new Paging();
	public final Paging getPaging() {
		return paging;
	}
	public final int getPage() {
		return paging.getPage();
	}
	public final void setPage(int page) {
		paging.setPage(page);
	}
	public final int getSize() {
		return paging.getSize();
	}
	public final void setSize(int size) {
		paging.setSize(size);
	}
	public final int getProjectId() {
		return projectId;
	}
	public final void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public final boolean isUnlinked() {
		return unlinked;
	}
	public final void setUnlinked(boolean unlinked) {
		this.unlinked = unlinked;
	}
	public final String getLike() {
		return like;
	}
	public final void setLike(String like) {
		if (like == null) {
			this.like = "";
		} else {
			this.like = like.trim();
		}
	}
	public boolean isPathbase() {
		return pathbase;
	}
	public void setPathbase(boolean pathbase) {
		this.pathbase = pathbase;
	}
}
