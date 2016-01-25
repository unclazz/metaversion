package org.unclazz.metaversion.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.MVApplication;
import org.unclazz.metaversion.service.BatchExecutorService;
import org.unclazz.metaversion.vo.OnlineBatchExecution;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;

@RestController
@RequestMapping(MVApplication.REST_API_PATH_PREFIX)
public class BatchesJsonController {
	@Autowired
	private BatchExecutorService executorService;
	
	/**
	 * オンラインバッチプログラム情報の一覧を返す.
	 * @param principal 認証情報
	 * @return オンラインバッチプログラム情報の一覧
	 */
	@RequestMapping(value="/batches", method=RequestMethod.GET)
	public Paginated<OnlineBatchExecution> getBathces(
			final Principal principal, 
			@ModelAttribute final Paging paging) {
		return executorService.getOnlineBatchExecutionList(paging);
	}
}
