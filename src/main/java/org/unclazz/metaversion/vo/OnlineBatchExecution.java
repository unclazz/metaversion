package org.unclazz.metaversion.vo;

import org.unclazz.metaversion.entity.IOnlineBatchProgram;
import org.unclazz.metaversion.entity.IOnlineBatchStatus;
import org.unclazz.metaversion.entity.OnlineBatchLog;

public class OnlineBatchExecution {
	private IOnlineBatchProgram program;
	private IOnlineBatchStatus lastStatus;
	private OnlineBatchLog lastLog;
	public final IOnlineBatchProgram getProgram() {
		return program;
	}
	public final void setProgram(IOnlineBatchProgram program) {
		this.program = program;
	}
	public final IOnlineBatchStatus getLastStatus() {
		return lastStatus;
	}
	public final void setLastStatus(IOnlineBatchStatus status) {
		this.lastStatus = status;
	}
	public OnlineBatchLog getLastLog() {
		return lastLog;
	}
	public void setLastLog(OnlineBatchLog lastLog) {
		this.lastLog = lastLog;
	}
}
