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
import org.unclazz.metaversion.entity.SvnCommitStats;
import org.unclazz.metaversion.service.CommitService;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;

import static org.unclazz.metaversion.MVUtils.*;

@RestController
@RequestMapping(MVApplication.REST_API_PATH_PREFIX)
public class ApiRepositoriesCommitStatsController {
	@Autowired
	private CommitService commitService;

	@RequestMapping(value="/repositories/{repositoryId}/commitstats", method=RequestMethod.GET)
	public Paginated<SvnCommitStats> getPaginated(final Principal principal,
			@PathVariable("repositoryId") final int repositoryId,
			@ModelAttribute final Paging paging) {
		return commitService.getCommitStatsListByRepositoryId(repositoryId, paging);
	}

	@RequestMapping(value="/repositories/{repositoryId}/commitstats/{commitId}", method=RequestMethod.GET)
	public ResponseEntity<SvnCommitStats> getOne(final Principal principal,
			@PathVariable("repositoryId") final int repositoryId,
			@PathVariable("commitId") final int commitId,
			@ModelAttribute final Paging paging) {
		return httpResponseOfOkOrNotFound(commitService.getCommitStatsByCommitId(commitId));
	}
}
