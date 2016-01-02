package org.unclazz.metaversion.vo;

import java.util.Date;

import org.unclazz.metaversion.MVUtils;
import org.unclazz.metaversion.entity.OnlineBatchProgram;
import org.unclazz.metaversion.entity.OnlineBatchStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

public final class BatchResult {
	public static BatchResult ofNowStarting(final OnlineBatchProgram program) {
		return new BatchResult(program, OnlineBatchStatus.RUNNING, new Date(), null, false, null);
	}
	
	private final OnlineBatchProgram program;
	private final OnlineBatchStatus status;
	private final Date start;
	private final Date end;
	private boolean successful;
	private final String message;
	
	private BatchResult(final OnlineBatchProgram program, final OnlineBatchStatus status,
			final Date start, final Date end, final boolean successful, final String message) {
		
		MVUtils.argsMustBeNotNull("Program, status and start", program, status, start);
		this.program = program;
		this.status = status;
		this.start = start;
		this.end = end;
		this.successful = successful;
		this.message = message;
	}

	public final OnlineBatchProgram getProgram() {
		return program;
	}
	public final OnlineBatchStatus getStatus() {
		return status;
	}
	@JsonFormat(pattern="yyyy/MM/dd HH:mm:ss.SSS")
	public final Date getStart() {
		return start;
	}
	@JsonFormat(pattern="yyyy/MM/dd HH:mm:ss.SSS")
	public final Date getEnd() {
		return end;
	}
	public final boolean isSuccessful() {
		return successful;
	}
	public final String getMessage() {
		return message;
	}
	public BatchResult andEnded() {
		return new BatchResult(program, OnlineBatchStatus.ENDED, start, new Date(), true, null);
	}
	public BatchResult andEnded(final String message) {
		return new BatchResult(program, OnlineBatchStatus.ENDED, start, new Date(), true, message);
	}
	public BatchResult andAbended(final String message) {
		return new BatchResult(program, OnlineBatchStatus.ABENDED, start, new Date(), false, message);
	}
	public BatchResult andAbended(final Throwable cause) {
		return new BatchResult(program, OnlineBatchStatus.ABENDED, start, new Date(), false, cause.getMessage());
	}
}
