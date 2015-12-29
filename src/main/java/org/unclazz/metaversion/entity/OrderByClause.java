package org.unclazz.metaversion.entity;

import java.util.regex.Pattern;

public class OrderByClause {
	public static enum Order {
		ASC, DESC;
		public String toString() { return this == ASC ? "ASC" : "DESC"; }
	}
	public static enum Nulls { 
		FIRST, LAST;
		public String toString() { return this == FIRST ? "NULLS FIRST" : "NULLS LAST"; }
	}
	private static final Pattern validPattern = Pattern.compile("^[A-Za-z0-9_]+$");
	public static OrderByClause noParticularOrder() {
		return new OrderByClause();
	}
	public static OrderByClause of(final CharSequence colName, final Order order, final Nulls nulls) {
		return new OrderByClause().and(colName, order, nulls);
	}
	public static OrderByClause of(final CharSequence colName, final Order order) {
		return new OrderByClause().and(colName, order);
	}
	public static OrderByClause of(final CharSequence colName) {
		return new OrderByClause().and(colName);
	}
	
	private final StringBuilder buff = new StringBuilder();
	private OrderByClause() {}
	
	public OrderByClause and(final CharSequence colName, final Order order, final Nulls nulls) {
		if (!validPattern.matcher(colName).matches()) {
			throw new IllegalArgumentException("Invalid column expression");
		}
		if (buff.length() > 0) {
			buff.append(',').append(' ');
		}
		buff.append(colName).append(' ').append(order).append(' ').append(nulls);
		return this;
	}
	public OrderByClause and(final CharSequence colName) {
		return and(colName, Order.ASC, Nulls.LAST);
	}
	public OrderByClause and(final CharSequence colName, final Order order) {
		return and(colName, order, order == Order.ASC ? Nulls.LAST : Nulls.FIRST);
	}
	@Override
	public String toString() {
		if (buff.length() == 0) {
			return " ";
		} else {
			return " ORDER BY " + buff.append(' ');
		}
	}
}
