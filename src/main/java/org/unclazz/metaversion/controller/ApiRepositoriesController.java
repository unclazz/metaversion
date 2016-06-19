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
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.MVApplication;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.SvnRepository;
import org.unclazz.metaversion.service.RepositoryService;
import org.unclazz.metaversion.service.SvnCommandService;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;
import org.unclazz.metaversion.vo.SvnRepositoryInfo;

import static org.unclazz.metaversion.MVUtils.*;

@RestController
@RequestMapping(MVApplication.REST_API_PATH_PREFIX)
public class ApiRepositoriesController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private SvnCommandService svnCommandService;
	
	/**
	 * リポジトリ情報の一覧を返す.
	 * @param principal 認証情報
	 * @param paging リクエストパラメータ{@code page}と{@code size}の情報を格納したオブジェクト
	 * @return リポジトリ情報の一覧
	 */
	@RequestMapping(value="/repositories", method=RequestMethod.GET)
	public Paginated<SvnRepository> getPaginated(final Principal principal,
			@ModelAttribute final Paging paging) {
		
		return repositoryService.getRepositoryList(paging);
	}
	
	/**
	 * IDで指定されたリポジトリ情報を返す.
	 * 該当するユーザ情報が見つからなかった場合は{@code 404 Not Found}を返す.
	 * @param principal 認証情報
	 * @param id ID
	 * @return リポジトリ情報
	 */
	@RequestMapping(value="/repositories/{id}", method=RequestMethod.GET)
	public ResponseEntity<SvnRepository> getOne(final Principal principal, @PathVariable("id") final int id) {
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
	public ResponseEntity<SvnRepository> put(final Principal principal,
			@PathVariable("id") final int id,
			@RequestBody final SvnRepository repository) {
		
		normalizeRepositoryInfo(repository);
		
		try {
			// 正規表現パターンの検証を行う
			Pattern.compile(repository.getTrunkPathPattern());
			Pattern.compile(repository.getBranchPathPattern());
			
		} catch (final PatternSyntaxException ex) {
			// 例外がスローされたら400 Bad Requestで返す
			logger.error("{}", stackTraceToCharSequence(ex));
			return httpResponseOfBadRequest(ex.getMessage());
		}
		
		// リクエストパラメータからVOを生成
		repositoryService.doPasswordEncode(repository);

		try {
			checkConnectivity(repository);
		} catch (final RuntimeException ex) {
			logger.error("{}", stackTraceToCharSequence(ex));
			return httpResponseOfBadRequest(ex.getMessage());
		}
		
		try {
			repositoryService.modifyRepository(repository, MVUserDetails.of(principal));
			return httpResponseOfOk(repository);
		} catch (final RuntimeException ex) {
			logger.error("{}", stackTraceToCharSequence(ex));
			return httpResponseOfInternalServerError(ex.getMessage());
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
	public ResponseEntity<SvnRepository> post(final Principal principal,
			@RequestBody SvnRepository repository) {
		
		normalizeRepositoryInfo(repository);
		
		try {
			// 正規表現パターンの検証を行う
			Pattern.compile(repository.getTrunkPathPattern());
			Pattern.compile(repository.getBranchPathPattern());
			
		} catch (final PatternSyntaxException ex) {
			// 例外がスローされたら400 Bad Requestで返す
			logger.error("{}", stackTraceToCharSequence(ex));
			return httpResponseOfBadRequest(ex.getMessage());
		}
		
		// リクエストパラメータからVOを生成
		repositoryService.doPasswordEncode(repository);

		try {
			checkConnectivity(repository);
		} catch (final RuntimeException ex) {
			logger.error("{}", stackTraceToCharSequence(ex));
			return httpResponseOfBadRequest(ex.getMessage());
		}
		
		try {
			repositoryService.registerRepository(repository, MVUserDetails.of(principal));
			return httpResponseOfOk(repository);
		} catch (final RuntimeException ex) {
			logger.error("{}", stackTraceToCharSequence(ex));
			return httpResponseOfInternalServerError(ex.getMessage());
		}
	}

	@RequestMapping(value="/repositories/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<SvnRepository> delete(final Principal principal, @PathVariable("id") final int id) {
		try {
			repositoryService.removeRepository(id, MVUserDetails.of(principal));
			return httpResponseOfOk();
			
		} catch (final RuntimeException e) {
			logger.error(e.getMessage());
			return httpResponseOfInternalServerError(e.getMessage());
		}
	}
	
	private void normalizeRepositoryInfo(final SvnRepository r) {
		r.setBaseUrl(r.getBaseUrl().replaceAll("/+$", ""));
	}
	
	private void checkConnectivity(final SvnRepository r) {
		try {
			logger.debug("リポジトリ接続を試行");
			final SvnRepositoryInfo info = svnCommandService.getRepositoryInfo(r, 2);
			logger.debug("リポジトリ接続 結果OK");
			logger.debug("ルートURL： {}", info.getRootUrl());
			logger.debug("UUID： {}", info.getUuid());
			logger.debug("HEADリビジョン： {}", info.getHeadRevision());
		} catch (final RuntimeException ex) {
			logger.debug("リポジトリ接続 結果NG：", ex);
			throw ex;
		}
	}
}
