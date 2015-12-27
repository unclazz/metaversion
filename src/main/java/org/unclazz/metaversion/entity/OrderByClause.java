package org.unclazz.metaversion.entity;

import java.util.regex.Pattern;

public class OrderByClause {
	private static final Pattern validPattern = Pattern.compile("^[A-Za-z0-9_]+$");
	private final StringBuilder buff = new StringBuilder();
	public OrderByClause appendSnakeCase(CharSequence colName) {
		append(colName);
		return this;
	}
	public OrderByClause append(HasColumnName colName) {
		append(colName.getColumnName());
		return this;
	}
	private void append(CharSequence cs) {
		if (!validPattern.matcher(cs).matches()) {
			throw new IllegalArgumentException("Invalid column expression");
		}
		if (buff.length() > 0) {
			buff.append(',').append(' ');
		}
		buff.append(cs);
	}
	@Override
	public String toString() {
		if (buff.length() == 0) {
			return " ";
		} else {
			return " order by " + buff.append(' ');
		}
	}
}
