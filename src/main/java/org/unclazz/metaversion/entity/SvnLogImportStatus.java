package org.unclazz.metaversion.entity;

public enum SvnLogImportStatus {
	RUNNING(1, "RUNNING"),
	ENDED(2, "ENDED"),
	ABENDED(3, "ABENDED"),
	QUITTED(4, "QUITTED");
	
	private final int id;
	private final String code;
	
	private SvnLogImportStatus(final int id, final String code) {
		this.id = id;
		this.code = code;
	}
	
	public int getId() {
		return id;
	}
	public String getCode() {
		return code;
	}
	
	public static SvnLogImportStatus valueOfCode(final String code) {
		for (final SvnLogImportStatus t : values()) {
			if (t.code.equals(code)) {
				return t;
			}
		}
		throw new IllegalArgumentException();
	}
}
