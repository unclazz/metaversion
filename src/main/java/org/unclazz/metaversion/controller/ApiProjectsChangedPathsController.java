package org.unclazz.metaversion.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.MVApplication;
import org.unclazz.metaversion.entity.ProjectChangedPath;
import org.unclazz.metaversion.service.CommitService;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;

@RestController
@RequestMapping(MVApplication.REST_API_PATH_PREFIX)
public class ApiProjectsChangedPathsController {
	@Autowired
	private CommitService commitService;
	
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
	public Paginated<ProjectChangedPath> getPaginated(final Principal principal,
			@PathVariable("id") final int id, @ModelAttribute final Paging paging) {
		return commitService.getChangedPathListByProjectId(id, paging);
	}
}
