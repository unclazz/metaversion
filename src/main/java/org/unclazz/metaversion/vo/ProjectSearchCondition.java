package org.unclazz.metaversion.vo;

public class ProjectSearchCondition {
	private boolean pathbase = false;
	private String like = "";
	private int unlinkedCommitId = 0;
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
	public final boolean isPathbase() {
		return pathbase;
	}
	public final void setPathbase(boolean pathbase) {
		this.pathbase = pathbase;
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
	public final int getUnlinkedCommitId() {
		return unlinkedCommitId;
	}
	public final void setUnlinkedCommitId(int unlinkedCommitId) {
		this.unlinkedCommitId = unlinkedCommitId;
	}
}
