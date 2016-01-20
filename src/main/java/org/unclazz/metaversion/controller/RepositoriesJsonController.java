package org.unclazz.metaversion.controller;

import java.security.Principal;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.MVApplication;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.Project;
import org.unclazz.metaversion.entity.SvnCommit;
import org.unclazz.metaversion.entity.SvnCommitPath;
import org.unclazz.metaversion.entity.SvnCommitStats;
import org.unclazz.metaversion.entity.SvnRepository;
import org.unclazz.metaversion.entity.SvnRepositoryStats;
import org.unclazz.metaversion.service.CommitService;
import org.unclazz.metaversion.service.ProjectService;
import org.unclazz.metaversion.service.RepositoryService;
import org.unclazz.metaversion.service.SvnCommandService;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;
import org.unclazz.metaversion.vo.SvnRepositoryInfo;

import static org.unclazz.metaversion.MVUtils.*;

@RestController
@RequestMapping(MVApplication.REST_API_PATH_PREFIX)
public class RepositoriesJsonController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private CommitService commitService;
	@Autowired
	private SvnCommandService svnCommandService;
	@Autowired
	private ProjectService projectService;
	
	/**
	 * リポジトリ情報の一覧を返す.
	 * @param principal 認証情報
	 * @param paging リクエストパラメータ{@code page}と{@code size}の情報を格納したオブジェクト
	 * @return リポジトリ情報の一覧
	 */
	@RequestMapping(value="/repositories", method=RequestMethod.GET)
	public Paginated<SvnRepository> getRepositoryList(final Principal principal,
			@ModelAttribute final Paging paging) {
		
		return repositoryService.getRepositoryList(paging);
	}
	
	/**
	 * リポジトリとその統計情報の一覧を返す.
	 * @param principal 認証情報
	 * @param paging リクエストパラメータ{@code page}と{@code size}の情報を格納したオブジェクト
	 * @return リポジトリとその統計情報の一覧
	 */
	@RequestMapping(value="/repositorystats", method=RequestMethod.GET)
	public Paginated<SvnRepositoryStats> getRepositoryStatsList(final Principal principal,
			@ModelAttribute final Paging paging) {
		
		return repositoryService.getRepositoryStatsList(paging);
	}
	@RequestMapping(value="/repositorystats/{id}", method=RequestMethod.GET)
	public ResponseEntity<SvnRepositoryStats> getRepositoryStats(final Principal principal,
			@PathVariable("id") final int id) {
		
		return httpResponseOfOkOrNotFound(repositoryService.getRepositoryStats(id));
	}
	
	/**
	 * IDで指定されたリポジトリ情報を返す.
	 * 該当するユーザ情報が見つからなかった場合は{@code 404 Not Found}を返す.
	 * @param principal 認証情報
	 * @param id ID
	 * @return リポジトリ情報
	 */
	@RequestMapping(value="/repositories/{id}", method=RequestMethod.GET)
	public ResponseEntity<SvnRepository> getRepository(final Principal principal, @PathVariable("id") final int id) {
		return httpResponseOfOkOrNotFound(repositoryService.getRepository(id));
	}
	
	/**
	 * リクエストパラメータをもとにリポジトリ情報を更新する.
	 * 正規表現パターンの不正やSVNリポジトリ接続チェックNGの場合{@code 400 Bad Request}を返す。
	 * 何らかの理由で更新に失敗した場合は{@code 500 Internal Server Error}を返す。
	 * 
	 * @param principal 認証情報
	 * @param id リポジトリID
	 * @param name リポジトリ名
	 * @param baseUrl ベースURL
	 * @param trunkPathPattern trunk部分パス正規表現パターン
	 * @param branchPathPattern branches部分パス正規表現パターン
	 * @param maxRevision 取り込み済み最大リビジョン番号
	 * @param username ユーザ名
	 * @param password パスワード
	 * @return 更新結果のリポジトリ情報
	 */
	@RequestMapping(value="/repositories/{id}", method=RequestMethod.PUT)
	public ResponseEntity<SvnRepository> putRepository(final Principal principal,
			@PathVariable("id") final int id,
			@RequestBody final SvnRepository repository) {
		
		try {
			// 正規表現パターンの検証を行う
			Pattern.compile(repository.getTrunkPathPattern());
			Pattern.compile(repository.getBranchPathPattern());
			
		} catch (final PatternSyntaxException ex) {
			// 例外がスローされたら400 Bad Requestで返す
			return httpResponseOfBadRequest(ex.getMessage());
		}
		
		// リクエストパラメータからVOを生成
		repositoryService.doPasswordEncode(repository);

		try {
			checkConnectivity(repository);
		} catch (final RuntimeException e) {
			return httpResponseOfBadRequest(e.getMessage());
		}
		
		try {
			repositoryService.modifyRepository(repository, MVUserDetails.of(principal));
			return httpResponseOfOk(repository);
		} catch (final RuntimeException e) {
			return httpResponseOfInternalServerError(e.getMessage());
		}
	}
	
	/**
	 * リクエストパラメータをもとにリポジトリ情報を登録する.
	 * 正規表現パターンの不正やSVNリポジトリ接続チェックNGの場合{@code 400 Bad Request}を返す。
	 * 何らかの理由で更新に失敗した場合は{@code 500 Internal Server Error}を返す。
	 * 取り込み済み最大リビジョン番号は登録直前に実際のSVN側の情報をもとに調整され、
	 * ベースURLが指すパスが存在し始めた最初（最古）のリビジョン番号で上書きされます。
	 * 
	 * @param principal 認証情報
	 * @param name リポジトリ名
	 * @param baseUrl ベースURL
	 * @param trunkPathPattern trunk部分パス正規表現パターン
	 * @param branchPathPattern branches部分パス正規表現パターン
	 * @param maxRevision 取り込み済み最大リビジョン番号
	 * @param username ユーザ名
	 * @param password パスワード
	 * @return 更新結果のリポジトリ情報
	 */
	@RequestMapping(value="/repositories", method=RequestMethod.POST)
	public ResponseEntity<SvnRepository> postRepository(final Principal principal,
			@RequestBody SvnRepository repository) {
		
		try {
			// 正規表現パターンの検証を行う
			Pattern.compile(repository.getTrunkPathPattern());
			Pattern.compile(repository.getBranchPathPattern());
			
		} catch (final PatternSyntaxException ex) {
			// 例外がスローされたら400 Bad Requestで返す
			return httpResponseOfBadRequest(ex.getMessage());
		}
		
		// リクエストパラメータからVOを生成
		repositoryService.doPasswordEncode(repository);

		try {
			checkConnectivity(repository);
		} catch (final RuntimeException e) {
			return httpResponseOfBadRequest(e.getMessage());
		}
		
		try {
			repositoryService.registerRepository(repository, MVUserDetails.of(principal));
			return httpResponseOfOk(repository);
		} catch (final RuntimeException e) {
			return httpResponseOfInternalServerError(e.getMessage());
		}
	}
	
	private void checkConnectivity(final SvnRepository r) {
		try {
			logger.debug("リポジトリ接続を試行");
			final SvnRepositoryInfo info = svnCommandService.getRepositoryInfo(r);
			logger.debug("リポジトリ接続 結果OK");
			logger.debug("ルートURL： {}", info.getRootUrl());
			logger.debug("UUID： {}", info.getUuid());
			logger.debug("HEADリビジョン： {}", info.getHeadRevision());
		} catch (final RuntimeException ex) {
			logger.debug("リポジトリ接続 結果NG：", ex);
			throw ex;
		}
	}

	@RequestMapping(value="/repositories/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<SvnRepository> deleteRepositories(final Principal principal, @PathVariable("id") final int id) {
		try {
			repositoryService.removeRepository(id, MVUserDetails.of(principal));
			return httpResponseOfOk();
			
		} catch (final RuntimeException e) {
			return httpResponseOfInternalServerError(e.getMessage());
		}
	}
	
	/**
	 * IDで指定されたリポジトリのコミット情報の一覧を返す.
	 * 
	 * @param principal 認証情報
	 * @param repositoryId リポジトリID
	 * @param unlinked プロジェクト紐付けされていないもののみを対象とするかどうか
	 * @param paging リクエストパラメータ{@code page}と{@code size}の情報を格納したオブジェクト
	 * @return コミット情報の一覧
	 */
	@RequestMapping(value="/repositories/{repositoryId}/commits", method=RequestMethod.GET)
	public Paginated<SvnCommit> getRepositoriesCommits(final Principal principal,
			@PathVariable("repositoryId") final int repositoryId,
			@RequestParam(value="unlinked", defaultValue="false") final boolean unlinked,
			@ModelAttribute final Paging paging) {
		if (unlinked) {
			return commitService.getProjectUndeterminedCommitList(repositoryId, paging);
		} else {
			return commitService.getCommitListByRepositoryId(repositoryId, paging);
		}
	}

	@RequestMapping(value="/repositories/{repositoryId}/commitstats", method=RequestMethod.GET)
	public Paginated<SvnCommitStats> getRepositoriesCommitStats(final Principal principal,
			@PathVariable("repositoryId") final int repositoryId,
			@ModelAttribute final Paging paging) {
		return commitService.getCommitStatsListByRepositoryId(repositoryId, paging);
	}

	@RequestMapping(value="/repositories/{repositoryId}/commits/{commitId}", method=RequestMethod.GET)
	public ResponseEntity<SvnCommit> getRepositoriesCommitsCommitId(final Principal principal,
			@PathVariable("repositoryId") final int repositoryId,
			@PathVariable("commitId") final int commitId) {
		return httpResponseOfOkOrNotFound(commitService.getCommitById(commitId));
	}
	
	@RequestMapping(value="/repositories/{repositoryId}/commits/{commitId}/projects", method=RequestMethod.GET)
	public Paginated<Project> getRepositoriesCommitsCommitIdProjects(final Principal principal,
			@PathVariable("repositoryId") final int repositoryId,
			@PathVariable("commitId") final int commitId,
			@ModelAttribute final Paging paging) {
		return projectService.getProjectListByCommitId(commitId, paging);
	}
	
	/**
	 * IDで指定されたコミットにより変更されたパスの一覧を返す.
	 * 
	 * @param principal 認証情報
	 * @param repositoryId リポジトリID
	 * @param commitId コミットID
	 * @param paging リクエストパラメータ{@code page}と{@code size}の情報を格納したオブジェクト
	 * @return コミットにより変更されたパスの一覧
	 */
	@RequestMapping(value="/repositories/{repositoryId}/commits/{commitId}/changedpaths", method=RequestMethod.GET)
	public Paginated<SvnCommitPath> getRepositorysCommitsChangedPaths(final Principal principal,
			@PathVariable("repositoryId") final int repositoryId,
			@PathVariable("commitId") final int commitId,
			@ModelAttribute final Paging paging) {
		
		return commitService.getChangedPathListByRepositoryIdAndCommitId(repositoryId, commitId, paging);
	}
}
