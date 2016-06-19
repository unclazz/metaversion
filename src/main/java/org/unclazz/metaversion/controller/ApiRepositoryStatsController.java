package org.unclazz.metaversion.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.MVApplication;
import org.unclazz.metaversion.entity.SvnRepositoryStats;
import org.unclazz.metaversion.service.RepositoryService;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;

import static org.unclazz.metaversion.MVUtils.*;

@RestController
@RequestMapping(MVApplication.REST_API_PATH_PREFIX)
public class ApiRepositoryStatsController {
	@Autowired
	private RepositoryService repositoryService;
	
	/**
	 * リポジトリとその統計情報の一覧を返す.
	 * @param principal 認証情報
	 * @param paging リクエストパラメータ{@code page}と{@code size}の情報を格納したオブジェクト
	 * @return リポジトリとその統計情報の一覧
	 */
	@RequestMapping(value="/repositorystats", method=RequestMethod.GET)
	public Paginated<SvnRepositoryStats> getPaginated(final Principal principal,
			@ModelAttribute final Paging paging) {
		
		return repositoryService.getRepositoryStatsList(paging);
	}
	@RequestMapping(value="/repositorystats/{id}", method=RequestMethod.GET)
	public ResponseEntity<SvnRepositoryStats> getOne(final Principal principal,
			@PathVariable("id") final int id) {
		
		return httpResponseOfOkOrNotFound(repositoryService.getRepositoryStats(id));
	}
}
