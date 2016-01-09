package org.unclazz.metaversion.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.MVApplication;
import org.unclazz.metaversion.MVUtils;
import org.unclazz.metaversion.service.MasterService;
import org.unclazz.metaversion.service.MasterService.ApplicationMayBeAlreadyInitialized;
import org.unclazz.metaversion.vo.MasterData;

@RestController
@RequestMapping(MVApplication.REST_API_PATH_PREFIX)
public class MasterDataJsonController {
	@Autowired
	private MasterService masterService;
	
	/**
	 * 各種マスターデータをまとめたオブジェクトを返す.
	 * 
	 * @param principal 認証情報
	 * @return オブジェクト
	 */
	@RequestMapping(value="/masterdata", method=RequestMethod.GET)
	public MasterData getMasterData(final Principal principal) {
		return MasterData.getInstance();
	}
	
	/**
	 * 各種マスターデータを登録する.
	 * 
	 * @param principal 認証情報
	 * @return 処理結果を表わすメッセージ
	 */
	@RequestMapping(value="/masterdata", method=RequestMethod.POST)
	public ResponseEntity<String> postMasterData(final Principal principal) {
		try {
			masterService.initializeMaster();
			return MVUtils.httpResponseOfOk("Succeeded.");
		} catch (final ApplicationMayBeAlreadyInitialized ex) {
			return MVUtils.httpResponseOfInternalServerError("Failed. Application may be already initialized...");
		} catch (final Exception e) {
			return MVUtils.httpResponseOfInternalServerError("Failed. Unexpected error has occurred.");
		}
	}
}
