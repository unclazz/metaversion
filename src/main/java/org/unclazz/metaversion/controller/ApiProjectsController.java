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
import org.unclazz.metaversion.entity.Project;
import org.unclazz.metaversion.service.P2CLinkerService;
import org.unclazz.metaversion.service.ProjectService;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.ProjectSearchCondition;

import static org.unclazz.metaversion.MVUtils.*;

@RestController
@RequestMapping(MVApplication.REST_API_PATH_PREFIX)
public class ApiProjectsController {
	@Autowired
	private ProjectService projectService;
	@Autowired
	private P2CLinkerService commitLinkService;
	
	/**
	 * 引数で指定された条件でプロジェクト情報を検索して返す.
	 * 
	 * @param principal 認証情報
	 * @param like 検索に使用される部分文字列
	 * @param pathBase パスにより検索するかどうか
	 * @param paging リクエストパラメータ{@code page}と{@code size}の情報を格納したオブジェクト
	 * @return プロジェクト情報の一覧
	 */
	@RequestMapping(value="/projects", method=RequestMethod.GET)
	public Paginated<Project> getPaginated(final Principal principal,
			@ModelAttribute final ProjectSearchCondition cond) {
		return projectService.getProjectListByCondition(cond);
	}
	
	/**
	 * IDで指定されたプロジェクト情報を取得して返す.
	 * 該当するプロジェクト情報が見つからなかった場合は{@code 404 Not Found}を返す.
	 * @param principal 認証情報
	 * @param id ID
	 * @return  プロジェクト情報
	 */
	@RequestMapping(value="/projects/{id}", method=RequestMethod.GET)
	public ResponseEntity<Project> getOne(final Principal principal, @PathVariable("id") final int id) {
		return httpResponseOfOkOrNotFound(projectService.getProjectById(id));
	}
	
	/**
	 * 引数で指定されたプロジェクト情報をもとに更新を行う.
	 * VOのIDプロパティはメソッド引数のIDで上書きされる。
	 * 何らかの理由で更新に失敗した場合は{@code 500 Internal Server Error}を返す。
	 * @param principal 認証情報
	 * @param id ID
	 * @param project プロジェクト情報
	 * @return 更新後のプロジェクト情報
	 */
	@RequestMapping(value="/projects/{id}", method=RequestMethod.PUT)
	public ResponseEntity<Project> put(final Principal principal,
			@PathVariable("id") final int id,
			@RequestBody final Project project) {
		
		try {
			final MVUserDetails auth = MVUserDetails.of(principal);
			project.setId(id);
			projectService.modifyProject(project, auth);
			if (project.getRedoCommitLink()) {
				commitLinkService.doP2CLinkSynchronously(project, auth);
			}
			return httpResponseOfOk(project);
		} catch (final RuntimeException e) {
			return httpResponseOfInternalServerError(e.getMessage());
		}
	}
	
	/**
	 * 引数で指定されたプロジェクト情報をもとに登録を行う.
	 * VOのIDプロパティはアプリケーションにより自動採番された値で上書きされる。
	 * 何らかの理由で更新に失敗した場合は{@code 500 Internal Server Error}を返す。
	 * 
	 * @param principal 認証情報
	 * @param project プロジェクト情報
	 * @return 登録後のプロジェクト情報
	 */
	@RequestMapping(value="/projects", method=RequestMethod.POST)
	public ResponseEntity<Project> post(final Principal principal,
			@RequestBody final Project project) {
		
		try {
			projectService.regisiterProject(project, MVUserDetails.of(principal));
			return httpResponseOfOk(project);
			
		} catch (final RuntimeException e) {
			return httpResponseOfInternalServerError(e.getMessage());
		}
	}
	
	@RequestMapping(value="/projects/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<Project> delete(final Principal principal, @PathVariable("id") final int id) {
		try {
			projectService.removeProjectById(id);
			return httpResponseOfOk();
			
		} catch (final RuntimeException e) {
			return httpResponseOfInternalServerError(e.getMessage());
		}
	}
}
