package org.unclazz.metaversion.vo;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Paginated<T> {
	private int currentPage;
	private int totalPage;
	private int totalSize;
	private int perPageSize;
	private List<T> list = new LinkedList<T>();
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public List<T> getList() {
		return list;
	}
	public void addAll(Collection<T> collection) {
		if (collection instanceof List) {
			list = (List<T>) collection;
		} else {
			list.addAll(collection);
		}
	}
	public void add(T element) {
		list.add(element);
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public int getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}
	public int getPerPageSize() {
		return perPageSize;
	}
	public void setPerPageSize(int perPageSize) {
		this.perPageSize = perPageSize;
	}
}
