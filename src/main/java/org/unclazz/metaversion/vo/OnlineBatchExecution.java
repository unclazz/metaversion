package org.unclazz.metaversion.vo;

import org.unclazz.metaversion.entity.OnlineBatchProgram;
import org.unclazz.metaversion.entity.OnlineBatchStatus;

public class OnlineBatchExecution {
	private OnlineBatchProgram program;
	private OnlineBatchStatus status;
	public final OnlineBatchProgram getProgram() {
		return program;
	}
	public final void setProgram(OnlineBatchProgram program) {
		this.program = program;
	}
	public final OnlineBatchStatus getStatus() {
		return status;
	}
	public final void setStatus(OnlineBatchStatus status) {
		this.status = status;
	}
}
