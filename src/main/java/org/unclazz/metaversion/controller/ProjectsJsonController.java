package org.unclazz.metaversion.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.MVApplication;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.ProjectChangedPath;
import org.unclazz.metaversion.entity.ProjectParallels;
import org.unclazz.metaversion.entity.ChangeType;
import org.unclazz.metaversion.entity.Project;
import org.unclazz.metaversion.entity.ProjectStats;
import org.unclazz.metaversion.entity.ProjectSvnCommit;
import org.unclazz.metaversion.entity.ProjectVirtualChangedPath;
import org.unclazz.metaversion.entity.SvnCommitWithRepositoryInfo;
import org.unclazz.metaversion.entity.VirtualChangedPath;
import org.unclazz.metaversion.service.P2CLinkerService;
import org.unclazz.metaversion.service.CommitService;
import org.unclazz.metaversion.service.ProjectParallelsService;
import org.unclazz.metaversion.service.ProjectService;
import org.unclazz.metaversion.service.VirtualChangedPathService;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;
import org.unclazz.metaversion.vo.ProjectCommitSearchCondition;
import org.unclazz.metaversion.vo.ProjectSearchCondition;

import static org.unclazz.metaversion.MVUtils.*;

@RestController
@RequestMapping(MVApplication.REST_API_PATH_PREFIX)
public class ProjectsJsonController {
	@Autowired
	private ProjectService projectService;
	@Autowired
	private CommitService commitService;
	@Autowired
	private P2CLinkerService commitLinkService;
	@Autowired
	private ProjectParallelsService parallelsService;
	@Autowired
	private VirtualChangedPathService virtualChangedPathService;
	
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
	public ResponseEntity<Project> getProjects(final Principal principal, @PathVariable("id") final int id) {
		return httpResponseOfOkOrNotFound(projectService.getProjectById(id));
	}
	
	/**
	 * IDで指定されたプロジェクトとその統計情報を取得して返す.
	 * 該当するプロジェクト情報が見つからなかった場合は{@code 404 Not Found}を返す.
	 * @param principal 認証情報
	 * @param id ID
	 * @return  プロジェクトとその統計情報
	 */
	@RequestMapping(value="/projectstats/{id}", method=RequestMethod.GET)
	public ResponseEntity<ProjectStats> getProjectsStats(final Principal principal, @PathVariable("id") final int id) {
		return httpResponseOfOkOrNotFound(projectService.getProjectStatsById(id));
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
	public ResponseEntity<Project> postProjects(final Principal principal,
			@RequestBody final Project project) {
		
		try {
			projectService.regisiterProject(project, MVUserDetails.of(principal));
			return httpResponseOfOk(project);
			
		} catch (final RuntimeException e) {
			return httpResponseOfInternalServerError(e.getMessage());
		}
	}
	
	@RequestMapping(value="/projects/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<Project> deleteProjects(final Principal principal, @PathVariable("id") final int id) {
		try {
			projectService.removeProjectById(id);
			return httpResponseOfOk();
			
		} catch (final RuntimeException e) {
			return httpResponseOfInternalServerError(e.getMessage());
		}
	}
	
	/**
	 * IDで指定されたプロジェクトに紐付けられたコミット情報もしくは紐付けられていないコミット情報の一覧を返す.
	 * 紐付け有無やページングの条件は第2引数の{@link ProjectCommitSearchCondition}を通じて設定する。
	 * 
	 * @param principal 認証情報
	 * @param projectId ID
	 * @param cond 検索条件
	 * @return コミット情報の一覧
	 */
	@RequestMapping(value="/projects/{projectId}/commits", method=RequestMethod.GET)
	public Paginated<SvnCommitWithRepositoryInfo> getProjectsCommits(final Principal principal,
			@PathVariable("projectId") final int projectId,
			@ModelAttribute final ProjectCommitSearchCondition cond) {
		
		cond.setProjectId(projectId);
		return commitService.getCommitListByCondition(cond);
	}

	@RequestMapping(value="/projects/{projectId}/commits/{commitId}", method=RequestMethod.GET)
	public ResponseEntity<SvnCommitWithRepositoryInfo> getProjectsCommits(final Principal principal,
			@PathVariable("projectId") final int projectId,
			@PathVariable("commitId") final int commitId) {
		return httpResponseOfOkOrNotFound(commitService.getCommitWithRepositoryInfoByCommitId(commitId));
	}

	@RequestMapping(value="/projects/{projectId}/parallels", method=RequestMethod.GET)
	public Paginated<ProjectParallels> getProjectsParallels(final Principal principal,
			@PathVariable("projectId") final int projectId, @ModelAttribute final Paging paging) {
		return parallelsService.getProjectParallelsByProjectId(projectId, paging);
	}

	/**
	 * IDで指定されたプロジェクトとコミットとを紐付ける.
	 * すでに紐付けが行われていた場合は{@code 400 Bad Request}を返す。
	 * 
	 * @param principal 認証情報
	 * @param projectId プロジェクトID
	 * @param commitId コミットID
	 * @return 登録された紐付け情報
	 */
	@RequestMapping(value="/projects/{projectId}/commits", method=RequestMethod.POST)
	public ResponseEntity<ProjectSvnCommit> postProjectsCommits(final Principal principal,
			@PathVariable("projectId") final int projectId, @RequestParam("commitId") final int commitId) {
		
		final ProjectSvnCommit vo = new ProjectSvnCommit();
		vo.setProjectId(projectId);
		vo.setCommitId(commitId);
		
		try {
			commitLinkService.registerCommitLink(vo, MVUserDetails.of(principal));
			return httpResponseOfOk(vo);
			
		} catch (final PersistenceException e) {
			return httpResponseOfBadRequest(e.getMessage());
		} catch (final RuntimeException e) {
			return httpResponseOfInternalServerError(e.getMessage());
		}
	}
	
	@RequestMapping(value="/projects/{projectId}/commits/{commitId}", method=RequestMethod.POST)
	public ResponseEntity<ProjectSvnCommit> postProjectsCommits2(final Principal principal,
			@PathVariable("projectId") final int projectId, @PathVariable("commitId") final int commitId) {
		return postProjectsCommits(principal, projectId, commitId);
	}
	
	/**
	 * IDで指定されたプロジェクトとコミットとの紐付けを解除する.
	 * 
	 * @param principal 認証情報
	 * @param projectId プロジェクトID
	 * @param commitId コミットID
	 * @return 削除された紐付け情報
	 */
	@RequestMapping(value="/projects/{projectId}/commits/{commitId}", method=RequestMethod.DELETE)
	public ResponseEntity<ProjectSvnCommit> deleteProjectsCommits(final Principal principal,
			@PathVariable("projectId") final int projectId, 
			@PathVariable("commitId") final int commitId) {
		
		final ProjectSvnCommit vo = new ProjectSvnCommit();
		vo.setProjectId(projectId);
		vo.setCommitId(commitId);
		
		try {
			commitLinkService.removeCommitLink(vo, MVUserDetails.of(principal));
			return httpResponseOfOk(vo);
		} catch (final Exception e) {
			return httpResponseOfInternalServerError(e.getMessage());
		}
	}
	
	/**
	 * IDで指定されたプロジェクトに紐付けられたコミットにより変更されたパスの一覧を返す.
	 * 結果の一覧では、同じファイルパスかつ同じリポジトリのものは1レコードに集約された状態になる。
	 * 一覧の要素はファイルパスとともにそれが属するリポジトリのIDと名称、
	 * さらにそれが最初／最後にプロジェクトの名義でコミットされたときのリビジョンと日付も保持している。
	 * 
	 * @param principal 認証情報
	 * @param id ID
	 * @param paging リクエストパラメータ{@code page}と{@code size}の情報を格納したオブジェクト
	 * @return パス情報の一覧
	 */
	@RequestMapping(value="/projects/{id}/changedpaths", method=RequestMethod.GET)
	public Paginated<ProjectChangedPath> getProjectsChangedPaths(final Principal principal,
			@PathVariable("id") final int id, @ModelAttribute final Paging paging) {
		return commitService.getChangedPathListByProjectId(id, paging);
	}

	@RequestMapping(value="/projects/{id}/virtualchangedpaths", method=RequestMethod.GET)
	public Paginated<ProjectVirtualChangedPath> getProjectsVirtualChangedPaths(final Principal principal,
			@PathVariable("id") final int id, @ModelAttribute final Paging paging) {
		return virtualChangedPathService.getPathListByProjectId(id, paging);
	}

	@RequestMapping(value="/projects/{projectId}/virtualchangedpaths/{virtualChangedPathId}", method=RequestMethod.GET)
	public ResponseEntity<ProjectVirtualChangedPath> getProjectsVirtualChangedPaths(final Principal principal,
			@PathVariable("projectId") final int projectId, 
			@PathVariable("virtualChangedPathId") final int virtualChangedPathId) {
	
		try {
			return httpResponseOfOk(virtualChangedPathService.getPathById(projectId, virtualChangedPathId));
		} catch (final Exception e) {
			return httpResponseOfInternalServerError(e.getMessage());
		}
	}

	@RequestMapping(value="/projects/{projectId}/virtualchangedpaths/{virtualChangedPathId}", method=RequestMethod.DELETE)
	public ResponseEntity<Void> deleteProjectsVirtualChangedPaths(final Principal principal,
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
	public ResponseEntity<VirtualChangedPath> postProjectsVirtualChangedPaths(final Principal principal,
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
