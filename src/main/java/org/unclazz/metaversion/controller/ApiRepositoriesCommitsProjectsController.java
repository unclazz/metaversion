package org.unclazz.metaversion.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.MVApplication;
import org.unclazz.metaversion.entity.Project;
import org.unclazz.metaversion.service.ProjectService;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;

@RestController
@RequestMapping(MVApplication.REST_API_PATH_PREFIX)
public class ApiRepositoriesCommitsProjectsController {
	@Autowired
	private ProjectService projectService;

	@RequestMapping(value="/repositories/{repositoryId}/commits/{commitId}/projects", method=RequestMethod.GET)
	public Paginated<Project> getPaginated(final Principal principal,
			@PathVariable("repositoryId") final int repositoryId,
			@PathVariable("commitId") final int commitId,
			@ModelAttribute final Paging paging) {
		return projectService.getProjectListByCommitId(commitId, paging);
	}
}
