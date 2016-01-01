package org.unclazz.metaversion.vo;

import java.util.List;

public class Paginated<T> {
	private int page;
	private int totalPage;
	private int totalSize;
	private int size;
	private List<T> list;
	Paginated() {}
	public int getPage() {
		return page;
	}
	void setPage(int currentPage) {
		this.page = currentPage;
	}
	public List<T> getList() {
		return list;
	}
	public void setList(List<T> list) {
		this.list = list;
	}
	public int getTotalPage() {
		return totalPage;
	}
	void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public int getTotalSize() {
		return totalSize;
	}
	void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}
	public int getSize() {
		return size;
	}
	void setSize(int size) {
		this.size = size;
	}
}
