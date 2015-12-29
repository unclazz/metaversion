package org.unclazz.metaversion.entity;

public enum DmlType {
	SELECT(1, "S", "SELECT"),
	INSERT(2, "I", "INSERT"),
	UPDATE(3, "U", "UPDATE"),
	DELETE(4, "D", "DELETE");
	
	private final int id;
	private final String code;
	private final String name;
	
	private DmlType(final int id, final String code, final String name) {
		this.id = id;
		this.code = code;
		this.name = name;
	}
	
	public final int getId() {
		return id;
	}
	public final String getCode() {
		return code;
	}
	public final String getName() {
		return name;
	}
	
	public static DmlType valueOfCode(final String code) {
		for (final DmlType t : values()) {
			if (t.code.equals(code)) {
				return t;
			}
		}
		throw new IllegalArgumentException();
	}
}
