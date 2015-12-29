package org.unclazz.metaversion.entity;

public class SvnLogImportStackTrace {
	private int svnLogImportId;
	private int lineNumber;
	private String line;
	public final int getSvnLogImportId() {
		return svnLogImportId;
	}
	public final void setSvnLogImportId(int svnLogImportId) {
		this.svnLogImportId = svnLogImportId;
	}
	public final int getLineNumber() {
		return lineNumber;
	}
	public final void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	public final String getLine() {
		return line;
	}
	public final void setLine(String line) {
		this.line = line;
	}
}
