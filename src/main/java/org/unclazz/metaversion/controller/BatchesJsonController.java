package org.unclazz.metaversion.controller;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.unclazz.metaversion.entity.IOnlineBatchProgram;
import org.unclazz.metaversion.entity.OnlineBatchProgram;
import org.unclazz.metaversion.service.BatchExecutorService.OnlineBatchRunnableFactory;
import org.unclazz.metaversion.service.CommitLinkService;
import org.unclazz.metaversion.service.LogImportService;
import org.unclazz.metaversion.vo.BatchResult;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;

import static org.unclazz.metaversion.MVUtils.*;

@RestController
@RequestMapping(MVApplication.REST_API_PATH_PREFIX)
public class BatchesJsonController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private LogImportService logImportService;
	@Autowired
	private CommitLinkService commitLinkService;
	
	/**
	 * オンラインバッチプログラム情報の一覧を返す.
	 * @param principal 認証情報
	 * @return オンラインバッチプログラム情報の一覧
	 */
	@RequestMapping(value="/batches", method=RequestMethod.GET)
	public Paginated<IOnlineBatchProgram> getBathces(
			final Principal principal, 
			@ModelAttribute final Paging paging) {
		
		final List<IOnlineBatchProgram> result = new LinkedList<IOnlineBatchProgram>();
		for (final IOnlineBatchProgram p : OnlineBatchProgram.values()) {
			result.add(p);
		}
		return Paginated.of(paging, result, result.size());
	}
	
	/**
	 * IDで指定されたオンラインバッチプログラム情報を返す.
	 * IDに対応するプログラムが見つからない場合は{@code 400 Bad Request}を返す。
	 * @param principal 認証情報
	 * @param id ID
	 * @return オンラインバッチプログラムの情報
	 */
	@RequestMapping(value="/batches/{id}", method=RequestMethod.GET)
	public ResponseEntity<IOnlineBatchProgram> getBathces(final Principal principal,
			@PathVariable("id") final int id) {
		
		for (final IOnlineBatchProgram p : OnlineBatchProgram.values()) {
			if (p.getId() == id) {
				return httpResponseOfOk(p);
			}
		}
		return httpResponseOfBadRequest("Unknown online batch id.");
			
	}
	
	/**
	 * IDで指定されたオンラインバッチプログラムを登録モードで起動する.
	 * IDに対応するプログラムが見つからない場合は{@code 400 Bad Request}を返す。
	 * バッチプログラムが正常終了しなかった場合は{@code 500 Internal Server Error}を返す。
	 * 
	 * @param principal 認証情報
	 * @param id バッチプログラムID
	 * @param arg0 引数0
	 * @param arg1 引数1
	 * @param arg2 引数2
	 * @return 実行結果
	 */
	@RequestMapping(value="/batches/{id}", method=RequestMethod.POST)
	public ResponseEntity<BatchResult> postBathces(final Principal principal,
			@PathVariable("id") final int id, 
			@RequestParam(value="arg0", defaultValue="0") final int arg0, 
			@RequestParam(value="arg1", defaultValue="") final String arg1, 
			@RequestParam(value="arg2", defaultValue="") final String arg2) {
		
		outer:
		for (final OnlineBatchProgram p : OnlineBatchProgram.values()) {
			if (p.getId() == id) {
				switch (p) {
				case LOG_IMPORTER:
					return getDevelBatchesLogImporter(principal, arg0);
				case P2C_LINKER:
					return getDevelBatchesP2cLinker(principal, arg0);
				default:
					break outer;
				}
			}
		}
		// 指定されたIDに該当するプログラムが見つからなかった場合はNG
		return httpResponseOfBadRequest("Unknown online batch id.");
	}
	
	@RequestMapping(value="/devel/batches/logimporter", method=RequestMethod.GET) // TODO 開発・検証用のサービス
	public ResponseEntity<BatchResult> getDevelBatchesLogImporter(final Principal principal,
			@RequestParam("repositoryId") final int repositoryId) {
		
		final BatchResult res = BatchResult.ofNowStarting(OnlineBatchProgram.LOG_IMPORTER);
		try {
			final OnlineBatchRunnableFactory factory = logImportService.
					getRunnableFactory(MVUserDetails.of(principal));
			factory.setArguments(repositoryId);
			factory.create().run();
			return httpResponseOfOk(res.andEnded());
			
		} catch (final RuntimeException e) {
			logger.error(String.format("Error has occurred at proccess of "
					+ "/rest/batches/logimport (repositoryId=%s).", repositoryId), e);
			return httpResponseOfInternalServerError(res.andAbended(e));
		}
	}
	
	@RequestMapping(value="/devel/batches/p2clinker", method=RequestMethod.GET) // TODO 開発・検証用のサービス
	public ResponseEntity<BatchResult> getDevelBatchesP2cLinker(final Principal principal,
			@RequestParam("projectId") final int projectId) {
		
		final BatchResult res = BatchResult.ofNowStarting(OnlineBatchProgram.P2C_LINKER);
		try {
			final OnlineBatchRunnableFactory factory = commitLinkService.
					getRunnableFactory(MVUserDetails.of(principal));
			factory.setArguments(projectId);
			factory.create().run();
			return httpResponseOfOk(res.andEnded());
			
		} catch (final RuntimeException e) {
			logger.error(String.format("Error has occurred at proccess of "
					+ "/rest/batches/commitlink (projectId=%s).", projectId), e);
			return httpResponseOfInternalServerError(res.andAbended(e));
		}
	}
}
