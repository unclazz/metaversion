package org.unclazz.metaversion.vo;

import java.util.regex.Pattern;

import org.unclazz.metaversion.MVUtils;
import org.unclazz.metaversion.entity.SvnRepository;

public final class SvnRepositoryPathInfo {
	public static SvnRepositoryPathInfo composedOf(final SvnRepositoryInfo svnInfo, final SvnRepository entity) {
		return new SvnRepositoryPathInfo(entity, svnInfo);
	}
	
	private final String rootUrl;
	private final String baseUrl;
	private final String baseUrlPathComponent;
	private final Pattern compiledTrunkPathPattern;
	private final Pattern compliedBranchPathPattern;
	private SvnRepositoryPathInfo(final SvnRepository entity, final SvnRepositoryInfo svnInfo) {
		rootUrl = svnInfo.getRootUrl();
		baseUrl = entity.getBaseUrl();
		
		// リビジョンのベースURLからパス部分を切り出す
		if (!entity.getBaseUrl().startsWith(rootUrl)) {
			throw MVUtils.illegalArgument("Invalid base url. "
					+ "The url does not start with repository's root url"
					+ "(base url=%s, root url=%s).", entity.getBaseUrl(), rootUrl);
		}
		baseUrlPathComponent = entity.getBaseUrl().substring(rootUrl.length());
		// trunkパス部分にマッチする正規表現パターンを初期化
		compiledTrunkPathPattern = Pattern.compile(entity.getTrunkPathPattern());
		// branchパス部分にマッチする正規表現パターンを初期化
		compliedBranchPathPattern = Pattern.compile(entity.getBranchPathPattern());
	}
	public final String getBaseUrlPathComponent() {
		return baseUrlPathComponent;
	}
	public final Pattern getCompiledTrunkPathPattern() {
		return compiledTrunkPathPattern;
	}
	public final Pattern getCompliedBranchPathPattern() {
		return compliedBranchPathPattern;
	}
	public final String getRootUrl() {
		return rootUrl;
	}
	public final String getBaseUrl() {
		return baseUrl;
	}
}
