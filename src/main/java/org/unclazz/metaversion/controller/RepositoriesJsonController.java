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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.SvnRepository;
import org.unclazz.metaversion.service.RepositoryService;
import org.unclazz.metaversion.service.SvnService;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;
import org.unclazz.metaversion.vo.SvnRepositoryInfo;

import static org.unclazz.metaversion.MVUtils.*;

@RestController
@RequestMapping("/rest")
public class RepositoriesJsonController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private SvnService svnService;
	
	@RequestMapping(value="/repositories", method=RequestMethod.GET)
	public Paginated<SvnRepository> getRepositoryLsit(final Principal principal, @ModelAttribute final Paging paging) {
		return Paginated.of(paging, repositoryService.getRepositoryList(paging),
				repositoryService.getRepositoryCount());
	}
	
	@RequestMapping(value="/repositories/{id}", method=RequestMethod.GET)
	public ResponseEntity<SvnRepository> getRepository(final Principal principal, @PathVariable("id") final int id) {
		return httpResponseOfOkOrNotFound(repositoryService.getRepository(id));
	}
	
	@RequestMapping(value="/repositories/{id}", method=RequestMethod.PUT)
	public ResponseEntity<SvnRepository> putRepository(final Principal principal,
			@PathVariable("id") final int id,
			@RequestParam("name") final String name,
			@RequestParam("baseUrl") final String baseUrl,
			@RequestParam(value="trunkPathPattern", defaultValue="/trunk") final String trunkPathPattern,
			@RequestParam(value="branchPathPattern", defaultValue="/branches/\\w+") final String branchPathPattern,
			@RequestParam(value="maxRevision", defaultValue="0") final int maxRevision,
			@RequestParam(value="username", required=false) final String username,
			@RequestParam(value="password", required=false) final char[] password) {
		
		try {
			// 正規表現パターンの検証を行う
			Pattern.compile(trunkPathPattern);
			Pattern.compile(branchPathPattern);
			
		} catch (final PatternSyntaxException ex) {
			// 例外がスローされたら400 Bad Requestで返す
			return httpResponseOfBadRequest(ex.getMessage());
		}
		
		// リクエストパラメータからVOを生成
		final SvnRepository repository = repositoryService.composeValueObject(id, name,
				baseUrl, trunkPathPattern, branchPathPattern, maxRevision, username, password);

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
	
	@RequestMapping(value="/repositories", method=RequestMethod.POST)
	public ResponseEntity<SvnRepository> postRepository(final Principal principal,
			@RequestParam("name") final String name,
			@RequestParam("baseUrl") final String baseUrl,
			@RequestParam(value="trunkPathPattern", defaultValue="/trunk") final String trunkPathPattern,
			@RequestParam(value="branchPathPattern", defaultValue="/branches/\\w+") final String branchPathPattern,
			@RequestParam(value="maxRevision", defaultValue="0") final int maxRevision,
			@RequestParam(value="username", required=false) final String username,
			@RequestParam(value="password", required=false) final char[] password) {
		
		try {
			// 正規表現パターンの検証を行う
			Pattern.compile(trunkPathPattern);
			Pattern.compile(branchPathPattern);
			
		} catch (final PatternSyntaxException ex) {
			// 例外がスローされたら400 Bad Requestで返す
			return httpResponseOfBadRequest(ex.getMessage());
		}
		
		// リクエストパラメータからVOを生成
		final SvnRepository repository = repositoryService.composeValueObject(name,
				baseUrl, trunkPathPattern, branchPathPattern, maxRevision, username, password);

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
			final SvnRepositoryInfo info = svnService.getRepositoryInfo(r);
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
	public ResponseEntity<SvnRepository> deleteRepository(final Principal principal, @PathVariable("id") final int id) {
		try {
			repositoryService.removeRepository(id, MVUserDetails.of(principal));
			return httpResponseOfOk();
			
		} catch (final RuntimeException e) {
			return httpResponseOfInternalServerError(e.getMessage());
		}
	}
}
