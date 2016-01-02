package org.unclazz.metaversion.vo;

public final class Paging {
	private int page = 1;
	private int size = 25;
	public final int getPage() {
		return page;
	}
	public final void setPage(int page) {
		this.page = page;
	}
	public final int getSize() {
		return size;
	}
	public final void setSize(int size) {
		this.size = size;
	}
}
