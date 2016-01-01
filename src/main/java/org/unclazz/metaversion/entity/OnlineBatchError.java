package org.unclazz.metaversion.entity;

public class OnlineBatchError {
	private int id;
	private int onlineBatchLogId;
	private String errorName;
	private String errorMessage;
	public final int getId() {
		return id;
	}
	public final void setId(int id) {
		this.id = id;
	}
	public final int getOnlineBatchLogId() {
		return onlineBatchLogId;
	}
	public final void setOnlineBatchLogId(int onlineBatchLogId) {
		this.onlineBatchLogId = onlineBatchLogId;
	}
	public final String getErrorName() {
		return errorName;
	}
	public final void setErrorName(String errorName) {
		this.errorName = errorName;
	}
	public final String getErrorMessage() {
		return errorMessage;
	}
	public final void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
