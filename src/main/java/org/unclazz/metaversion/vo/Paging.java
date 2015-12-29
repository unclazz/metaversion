package org.unclazz.metaversion.vo;

public class Paging {
	public static final int DEFAULT_SIZE = 25;
	public static Paging of(final int page, final int size) {
		return new Paging(page, size);
	}
	public static Paging ofPage(final int page) {
		return new Paging(page, DEFAULT_SIZE);
	}
	public static Paging ofSize(final int size) {
		return new Paging(1, size);
	}
	
	private final int perPageSize;
	private final int page;
	private Paging(final int page, final int size) {
		if (page < 1 || size < 1) {
			throw new IllegalArgumentException();
		}
		this.page = page;
		this.perPageSize = size;
	}
	public int getSize() {
		return perPageSize;
	}
	public int getPage() {
		return page;
	}
}
