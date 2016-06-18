package org.unclazz.metaversion.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.MVApplication;
import org.unclazz.metaversion.service.CommitService;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;

@RestController
@RequestMapping(MVApplication.REST_API_PATH_PREFIX)
public class ApiRepositoriesPathNamesController {
	@Autowired
	private CommitService commitService;
	
	@RequestMapping(value="/repositories/{repositoryId}/pathnames", method=RequestMethod.GET)
	public Paginated<String> getPaginated(final Principal principal,
			@PathVariable("repositoryId") final int repositoryId,
			@RequestParam(value="unlinkedTo", required=false, defaultValue="0") final int unlinkedTo, 
			@RequestParam("like") final String like, 
			@ModelAttribute final Paging paging) {
		return commitService.getChangedPathListByRepositoryIdAndPartialPath
				(repositoryId, like == null ? "" : like.trim(), unlinkedTo, paging);
	}
}
