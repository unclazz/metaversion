package org.unclazz.metaversion.vo;

import java.util.List;

public final class Paginated<T> {
	public static<T> Paginated<T> of(Paging paging, List<T> list, int total) {
		return new Paginated<T>(paging, list, total);
	}
	
	private final int page;
	private final int totalPage;
	private final int totalSize;
	private final int size;
	private final List<T> list;
	
	private Paginated(final Paging paging, final List<T> list, final int total) {
		if (paging == null || list == null || total < 0) {
			throw new IllegalArgumentException();
		}
		this.list = list;
		this.page = paging.getPage();
		this.size = paging.getSize();
		this.totalPage = total / paging.getSize() + (total % paging.getSize() == 0 ? 0 : 1);
		this.totalSize = total;
	}
	
	public int getPage() {
		return page;
	}
	public List<T> getList() {
		return list;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public int getTotalSize() {
		return totalSize;
	}
	public int getSize() {
		return size;
	}
}
