package org.unclazz.metaversion.controller;

import java.security.Principal;

import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.MVApplication;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.ProjectSvnCommit;
import org.unclazz.metaversion.entity.SvnCommitWithRepositoryInfo;
import org.unclazz.metaversion.service.P2CLinkerService;
import org.unclazz.metaversion.service.CommitService;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.ProjectCommitSearchCondition;

import static org.unclazz.metaversion.MVUtils.*;

@RestController
@RequestMapping(MVApplication.REST_API_PATH_PREFIX)
public class ApiProjectsCommitsController {
	@Autowired
	private CommitService commitService;
	@Autowired
	private P2CLinkerService commitLinkService;
	
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
	public Paginated<SvnCommitWithRepositoryInfo> getPaginated(final Principal principal,
			@PathVariable("projectId") final int projectId,
			@ModelAttribute final ProjectCommitSearchCondition cond) {
		
		cond.setProjectId(projectId);
		return commitService.getCommitListByCondition(cond);
	}

	@RequestMapping(value="/projects/{projectId}/commits/{commitId}", method=RequestMethod.GET)
	public ResponseEntity<SvnCommitWithRepositoryInfo> getOne(final Principal principal,
			@PathVariable("projectId") final int projectId,
			@PathVariable("commitId") final int commitId) {
		return httpResponseOfOkOrNotFound(commitService.getCommitWithRepositoryInfoByCommitId(commitId));
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
	public ResponseEntity<ProjectSvnCommit> postWithCommitIdRequestParam(final Principal principal,
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
	public ResponseEntity<ProjectSvnCommit> postWithCommitIdPathVariable(final Principal principal,
			@PathVariable("projectId") final int projectId, @PathVariable("commitId") final int commitId) {
		return postWithCommitIdRequestParam(principal, projectId, commitId);
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
	public ResponseEntity<ProjectSvnCommit> delete(final Principal principal,
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
}
