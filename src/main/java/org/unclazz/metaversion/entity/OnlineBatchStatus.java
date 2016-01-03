package org.unclazz.metaversion.entity;

public enum OnlineBatchStatus implements IOnlineBatchStatus {
	RUNNING(1, "Running"),
	ENDED(2, "Ended"),
	ABENDED(3, "Abended"),
	QUITTED(4, "Quitted");
	
	private final int id;
	private final String name;
	
	private OnlineBatchStatus(final int id, final String name) {
		this.id = id;
		this.name = name;
	}
	
	@Override
	public int getId() {
		return id;
	}
	@Override
	public String getStatusName() {
		return name;
	}
	
	public static OnlineBatchStatus valueOfStatusName(final String name) {
		for (final OnlineBatchStatus v : values()) {
			if (v.name.equals(name)) {
				return v;
			}
		}
		throw new IllegalArgumentException();
	}
}
