package org.unclazz.metaversion.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.MVApplication;
import org.unclazz.metaversion.service.PathNameService;

@RestController
@RequestMapping(MVApplication.REST_API_PATH_PREFIX)
public class ApiPathNamesController {
	@Autowired
	private PathNameService pathNameService;
	
	/**
	 * 引数で指定された部分文字列を使用してプロジェクトの名義でコミットされているファイルパスを検索してその名称の一覧を返す.
	 * 一覧の要素は名前昇順でソートされる。一覧の要素数は引数で指定されたサイズ以下となる。
	 * 部分文字列として空文字列を渡したり、サイズとして0以下の値を指定した場合は空の一覧が返される。
	 * @param principal 認証情報
	 * @param like 部分文字列
	 * @param size サイズ
	 * @return 名前の一覧
	 */
	@RequestMapping(value="/pathnames", method=RequestMethod.GET)
	public List<String> getList(final Principal principal,
			@RequestParam("like") final String like, 
			@RequestParam("size") final int size) {
		final String trimmed = like.trim();
		if (trimmed.isEmpty() || size < 1) {
			return Collections.emptyList();
		} else {
			return pathNameService.getPathNameList(like.trim(), size);
		}
	}
}
