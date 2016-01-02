package org.unclazz.metaversion.entity;

public enum OnlineBatchProgram {
	LOG_IMPORT(1, "LogImport"),
	COMMIT_LINK(2, "CommitLink");
	
	private final int id;
	private final String programName;
	
	private OnlineBatchProgram(final int id, final String name) {
		this.id = id;
		this.programName = name;
	}
	
	public int getId() {
		return id;
	}
	public String getProgramName() {
		return programName;
	}
	
	public static OnlineBatchProgram valueOfProgramName(final String name) {
		for (final OnlineBatchProgram v : values()) {
			if (v.programName.equals(name)) {
				return v;
			}
		}
		throw new IllegalArgumentException();
	}
}
