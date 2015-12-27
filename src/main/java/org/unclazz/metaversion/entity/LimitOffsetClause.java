package org.unclazz.metaversion.entity;

public class LimitOffsetClause {
	private int limit = 0;
	private int offset = 0;
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	@Override
	public String toString() {
		if (limit < 1) {
			return " ";
		} else if (offset < 1) {
			return " limit " + limit + ' '; 
		} else {
			return " limit " + limit + " offset " + offset + ' ';
		}
	}
}
