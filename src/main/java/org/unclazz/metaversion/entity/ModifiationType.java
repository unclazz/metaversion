package org.unclazz.metaversion.entity;

public enum ModifiationType {
	ADD(1, "A", "ADD"),
	DELETE(2, "D", "DELETE"),
	MODIFY(3, "M", "MODIFY"),
	REPLACE(4, "R", "REPLACE");
	
	private final int id;
	private final String code;
	private final String name;
	
	private ModifiationType(final int id, final String code, final String name) {
		this.id = id;
		this.code = code;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	public String getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	
	public static ModifiationType valueOfCode(final String code) {
		for (final ModifiationType t : values()) {
			if (t.code.equals(code)) {
				return t;
			}
		}
		throw new IllegalArgumentException();
	}
}
