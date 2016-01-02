package org.unclazz.metaversion.controller;

import java.security.Principal;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;
import static org.unclazz.metaversion.MVUtils.*;

@RestController
@RequestMapping("/rest")
public class RepositoriesJsonController {
	@Autowired
	private RepositoryService repositoryService;
	
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
			@RequestParam("trunkPathPattern") final String trunkPathPattern,
			@RequestParam("branchPathPattern") final String branchPathPattern,
			@RequestParam("maxRevision") final int maxRevision,
			@RequestParam("username") final String username,
			@RequestParam("password") final char[] password) {
		
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
			@RequestParam("trunkPathPattern") final String trunkPathPattern,
			@RequestParam("branchPathPattern") final String branchPathPattern,
			@RequestParam("maxRevision") final int maxRevision,
			@RequestParam("username") final String username,
			@RequestParam("password") final char[] password) {
		
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
			repositoryService.registerRepository(repository, MVUserDetails.of(principal));
			return httpResponseOfOk(repository);
		} catch (final RuntimeException e) {
			return httpResponseOfInternalServerError(e.getMessage());
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
