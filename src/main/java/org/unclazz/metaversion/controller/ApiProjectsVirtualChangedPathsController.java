package org.unclazz.metaversion.controller;

import java.security.Principal;

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
import org.unclazz.metaversion.entity.ChangeType;
import org.unclazz.metaversion.entity.ProjectVirtualChangedPath;
import org.unclazz.metaversion.entity.VirtualChangedPath;
import org.unclazz.metaversion.service.VirtualChangedPathService;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;

import static org.unclazz.metaversion.MVUtils.*;

@RestController
@RequestMapping(MVApplication.REST_API_PATH_PREFIX)
public class ApiProjectsVirtualChangedPathsController {
	@Autowired
	private VirtualChangedPathService virtualChangedPathService;

	@RequestMapping(value="/projects/{id}/virtualchangedpaths", method=RequestMethod.GET)
	public Paginated<ProjectVirtualChangedPath> getPaginated(final Principal principal,
			@PathVariable("id") final int id, @ModelAttribute final Paging paging) {
		return virtualChangedPathService.getPathListByProjectId(id, paging);
	}

	@RequestMapping(value="/projects/{projectId}/virtualchangedpaths/{virtualChangedPathId}", method=RequestMethod.GET)
	public ResponseEntity<ProjectVirtualChangedPath> getOne(final Principal principal,
			@PathVariable("projectId") final int projectId, 
			@PathVariable("virtualChangedPathId") final int virtualChangedPathId) {
	
		try {
			return httpResponseOfOk(virtualChangedPathService.getPathById(projectId, virtualChangedPathId));
		} catch (final Exception e) {
			return httpResponseOfInternalServerError(e.getMessage());
		}
	}

	@RequestMapping(value="/projects/{projectId}/virtualchangedpaths/{virtualChangedPathId}", method=RequestMethod.DELETE)
	public ResponseEntity<Void> delete(final Principal principal,
			@PathVariable("projectId") final int projectId, 
			@PathVariable("virtualChangedPathId") final int virtualChangedPathId) {
	
		try {
			virtualChangedPathService.removePath(virtualChangedPathId, MVUserDetails.of(principal));
			return httpResponseOfOk();
		} catch (final Exception e) {
			return httpResponseOfInternalServerError(e.getMessage());
		}
	}

	@RequestMapping(value="/projects/{projectId}/virtualchangedpaths", method=RequestMethod.POST)
	public ResponseEntity<VirtualChangedPath> post(final Principal principal,
			@PathVariable("projectId") final int projectId,
			@RequestBody final VirtualChangedPath virtualChangedPath) {

		virtualChangedPath.setProjectId(projectId);
		virtualChangedPath.setChangeTypeId(ChangeType.MODIFY.getId());
		
		try {
			virtualChangedPathService.registerPath(virtualChangedPath, MVUserDetails.of(principal));
			return httpResponseOfOk(virtualChangedPath);
		} catch (final Exception e) {
			return httpResponseOfInternalServerError(e.getMessage());
		}
	}
}
