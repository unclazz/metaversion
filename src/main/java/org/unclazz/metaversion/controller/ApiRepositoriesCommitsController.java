package org.unclazz.metaversion.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.MVApplication;
import org.unclazz.metaversion.entity.SvnCommit;
import org.unclazz.metaversion.service.CommitService;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;

import static org.unclazz.metaversion.MVUtils.*;

@RestController
@RequestMapping(MVApplication.REST_API_PATH_PREFIX)
public class ApiRepositoriesCommitsController {
	@Autowired
	private CommitService commitService;
	
	/**
	 * IDで指定されたリポジトリのコミット情報の一覧を返す.
	 * 
	 * @param principal 認証情報
	 * @param repositoryId リポジトリID
	 * @param unlinked プロジェクト紐付けされていないもののみを対象とするかどうか
	 * @param paging リクエストパラメータ{@code page}と{@code size}の情報を格納したオブジェクト
	 * @return コミット情報の一覧
	 */
	@RequestMapping(value="/repositories/{repositoryId}/commits", method=RequestMethod.GET)
	public Paginated<SvnCommit> getPaginated(final Principal principal,
			@PathVariable("repositoryId") final int repositoryId,
			@RequestParam(value="unlinked", defaultValue="false") final boolean unlinked,
			@ModelAttribute final Paging paging) {
		if (unlinked) {
			return commitService.getProjectUndeterminedCommitList(repositoryId, paging);
		} else {
			return commitService.getCommitListByRepositoryId(repositoryId, paging);
		}
	}

	@RequestMapping(value="/repositories/{repositoryId}/commits/{commitId}", method=RequestMethod.GET)
	public ResponseEntity<SvnCommit> getOne(final Principal principal,
			@PathVariable("repositoryId") final int repositoryId,
			@PathVariable("commitId") final int commitId) {
		return httpResponseOfOkOrNotFound(commitService.getCommitById(commitId));
	}
}
