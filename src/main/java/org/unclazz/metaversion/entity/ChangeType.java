package org.unclazz.metaversion.entity;

public enum ChangeType implements IChangeType {
	ADD(1, "A", "Added"),
	DELETE(2, "D", "Deleted"),
	MODIFY(3, "M", "Modified"),
	REPLACE(4, "R", "Replaced");
	
	private final int id;
	private final String code;
	private final String name;
	
	private ChangeType(final int id, final String code, final String name) {
		this.id = id;
		this.code = code;
		this.name = name;
	}
	
	@Override
	public int getId() {
		return id;
	}
	@Override
	public String getCode() {
		return code;
	}
	@Override
	public String getTypeName() {
		return name;
	}
	
	public static ChangeType valueOfCode(final String code) {
		for (final ChangeType t : values()) {
			if (t.code.equals(code)) {
				return t;
			}
		}
		throw new IllegalArgumentException();
	}
	
	public static ChangeType valueOfCode(final char code) {
		for (final ChangeType t : values()) {
			if (t.code.charAt(0) == code) {
				return t;
			}
		}
		throw new IllegalArgumentException();
	}
}
