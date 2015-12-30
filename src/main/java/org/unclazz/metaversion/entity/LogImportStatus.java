package org.unclazz.metaversion.entity;

public enum LogImportStatus {
	RUNNING(1, "RUNNING"),
	ENDED(2, "ENDED"),
	ABENDED(3, "ABENDED"),
	QUITTED(4, "QUITTED");
	
	private final int id;
	private final String code;
	
	private LogImportStatus(final int id, final String code) {
		this.id = id;
		this.code = code;
	}
	
	public int getId() {
		return id;
	}
	public String getCode() {
		return code;
	}
	
	public static LogImportStatus valueOfCode(final String code) {
		for (final LogImportStatus t : values()) {
			if (t.code.equals(code)) {
				return t;
			}
		}
		throw new IllegalArgumentException();
	}
}
