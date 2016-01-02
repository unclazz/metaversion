package org.unclazz.metaversion.vo;

import org.tmatesoft.svn.core.wc.SVNInfo;

public final class SvnRepositoryInfo {
	public static SvnRepositoryInfo of(final SVNInfo info) {
		return new SvnRepositoryInfo(info.getRepositoryRootURL().toDecodedString(),
				info.getRepositoryUUID(), (int) info.getRevision().getNumber());
	}
	
	private final String rootUrl;
	private final String uuid;
	private final int headRevision;
	
	private SvnRepositoryInfo(final String url, final String uuid, final int headRevision) {
		this.rootUrl = url;
		this.uuid = uuid;
		this.headRevision = headRevision;
	}

	public String getRootUrl() {
		return rootUrl;
	}
	public String getUuid() {
		return uuid;
	}
	public int getHeadRevision() {
		return headRevision;
	}
}
