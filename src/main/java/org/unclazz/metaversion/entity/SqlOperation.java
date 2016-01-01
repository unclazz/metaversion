package org.unclazz.metaversion.entity;

public enum SqlOperation {
	SELECT(1, "S", "SELECT"),
	INSERT(2, "I", "INSERT"),
	UPDATE(3, "U", "UPDATE"),
	DELETE(4, "D", "DELETE");
	
	private final int id;
	private final String code;
	private final String name;
	
	private SqlOperation(final int id, final String code, final String name) {
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
	public String getOperationName() {
		return name;
	}
	
	public static SqlOperation valueOfCode(final String code) {
		for (final SqlOperation t : values()) {
			if (t.code.equals(code)) {
				return t;
			}
		}
		throw new IllegalArgumentException();
	}
}
