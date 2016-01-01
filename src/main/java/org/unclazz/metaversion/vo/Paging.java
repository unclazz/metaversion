package org.unclazz.metaversion.vo;

import java.util.List;

public class Paging {
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
	public<T> Paginated<T> bind(final List<T> list, final int total) {
		final Paginated<T> result = new Paginated<T>();
		result.setList(list);
		result.setPage(page);
		result.setSize(size);
		result.setTotalPage(total / size + (total % size == 0 ? 0 : 1));
		result.setTotalSize(total);
		return result;
	}
}
