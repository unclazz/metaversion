package org.unclazz.metaversion.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.MVApplication;
import org.unclazz.metaversion.entity.ProjectParallels;
import org.unclazz.metaversion.service.ProjectParallelsService;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;

@RestController
@RequestMapping(MVApplication.REST_API_PATH_PREFIX)
public class ApiProjectsParallelsController {
	@Autowired
	private ProjectParallelsService parallelsService;

	@RequestMapping(value="/projects/{projectId}/parallels", method=RequestMethod.GET)
	public Paginated<ProjectParallels> getPaginated(final Principal principal,
			@PathVariable("projectId") final int projectId, @ModelAttribute final Paging paging) {
		return parallelsService.getProjectParallelsByProjectId(projectId, paging);
	}
}
