package org.unclazz.metaversion.entity;

public class SvnCommitPathWithBranchName extends SvnCommitPath {
	private String branchName;
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
}
