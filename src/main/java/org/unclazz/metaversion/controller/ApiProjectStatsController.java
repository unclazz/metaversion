package org.unclazz.metaversion.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.MVApplication;
import org.unclazz.metaversion.entity.ProjectStats;
import org.unclazz.metaversion.service.ProjectService;

import static org.unclazz.metaversion.MVUtils.*;

@RestController
@RequestMapping(MVApplication.REST_API_PATH_PREFIX)
public class ApiProjectStatsController {
	@Autowired
	private ProjectService projectService;
	
	/**
	 * IDで指定されたプロジェクトとその統計情報を取得して返す.
	 * 該当するプロジェクト情報が見つからなかった場合は{@code 404 Not Found}を返す.
	 * @param principal 認証情報
	 * @param id ID
	 * @return  プロジェクトとその統計情報
	 */
	@RequestMapping(value="/projectstats/{id}", method=RequestMethod.GET)
	public ResponseEntity<ProjectStats> getOne(final Principal principal, @PathVariable("id") final int id) {
		return httpResponseOfOkOrNotFound(projectService.getProjectStatsById(id));
	}
}
