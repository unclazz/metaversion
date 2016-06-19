package org.unclazz.metaversion.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.MVApplication;
import org.unclazz.metaversion.entity.SvnCommitPathWithBranchName;
import org.unclazz.metaversion.service.CommitService;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;

@RestController
@RequestMapping(MVApplication.REST_API_PATH_PREFIX)
public class ApiRepositoriesCommitsChangedPathsController {
	@Autowired
	private CommitService commitService;
	
	/**
	 * IDで指定されたコミットにより変更されたパスの一覧を返す.
	 * 
	 * @param principal 認証情報
	 * @param repositoryId リポジトリID
	 * @param commitId コミットID
	 * @param paging リクエストパラメータ{@code page}と{@code size}の情報を格納したオブジェクト
	 * @return コミットにより変更されたパスの一覧
	 */
	@RequestMapping(value="/repositories/{repositoryId}/commits/{commitId}/changedpaths", method=RequestMethod.GET)
	public Paginated<SvnCommitPathWithBranchName> getPaginated(final Principal principal,
			@PathVariable("repositoryId") final int repositoryId,
			@PathVariable("commitId") final int commitId,
			@ModelAttribute final Paging paging) {
		
		return commitService.getChangedPathListByRepositoryIdAndCommitId(repositoryId, commitId, paging);
	}
}
