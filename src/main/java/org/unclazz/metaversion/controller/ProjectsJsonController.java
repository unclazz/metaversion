package org.unclazz.metaversion.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.Project;
import org.unclazz.metaversion.service.ProjectService;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;
import static org.unclazz.metaversion.MVUtils.*;

@RestController
@RequestMapping("/rest")
public class ProjectsJsonController {
	@Autowired
	private ProjectService projectService;
	
	/**
	 * 引数で指定された部分文字列を使用してプロジェクトを検索してその名称の一覧を返す.
	 * 一覧の要素は名前昇順でソートされる。一覧の要素数は引数で指定されたサイズ以下となる。
	 * 部分文字列として空文字列を渡したり、サイズとして0以下の値を指定した場合は空の一覧が返される。
	 * @param principal 認証情報
	 * @param like 部分文字列
	 * @param size サイズ
	 * @return 名前の一覧
	 */
	@RequestMapping(value="/projectnames", method=RequestMethod.GET)
	public List<String> getProjectNames(final Principal principal,
			@RequestParam("like") final String like, 
			@RequestParam("size") final int size) {
		final String trimmed = like.trim();
		if (trimmed.isEmpty() || size < 1) {
			return Collections.emptyList();
		} else {
			return projectService.getProjectNameList(like.trim(), size);
		}
	}
	
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
	public Paginated<Project> getProjects(final Principal principal,
			@RequestParam(value="like", defaultValue="") final String like,
			@RequestParam(value="pathbase", defaultValue="false") final boolean pathBase,
			@ModelAttribute final Paging paging) {
		final String likeTrimmed = like.trim();
		if (likeTrimmed.isEmpty()) {
			return projectService.getProjectListAll(paging);
		} else if (pathBase) {
			return projectService.getProjectListByPartialPath(likeTrimmed, paging);
		} else {
			return projectService.getProjectListByPartialName(likeTrimmed, paging);
		}
		
	}
	
	/**
	 * IDで指定されたプロジェクト情報を取得して返す.
	 * 該当するプロジェクト情報が見つからなかった場合は{@code 404 Not Found}を返す.
	 * @param principal 認証情報
	 * @param id ID
	 * @return  プロジェクト情報
	 */
	@RequestMapping(value="/projects/{id}", method=RequestMethod.GET)
	public ResponseEntity<Project> getUser(final Principal principal, @PathVariable("id") final int id) {
		return httpResponseOfOkOrNotFound(projectService.getProjectByProjectId(id));
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
	public ResponseEntity<Project> putProjects(final Principal principal,
			@PathVariable("id") final int id,
			@RequestBody final Project project) {
		
		try {
			project.setId(id);
			projectService.modifyProject(project, MVUserDetails.of(principal));
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
	public ResponseEntity<Project> postProjects(final Principal principal,
			@RequestBody final Project project) {
		
		try {
			projectService.regisiterProject(project, MVUserDetails.of(principal));
			return httpResponseOfOk(project);
			
		} catch (final RuntimeException e) {
			return httpResponseOfInternalServerError(e.getMessage());
		}
	}
	
	// TODO 外部キー制約で縛られたレコードをどのようにするかが課題
	@RequestMapping(value="/projects/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<Project> deleteProjects(final Principal principal, @PathVariable("id") final int id) {
		try {
			// TODO
			return httpResponseOfOk();
			
		} catch (final RuntimeException e) {
			return httpResponseOfInternalServerError(e.getMessage());
		}
	}
}
