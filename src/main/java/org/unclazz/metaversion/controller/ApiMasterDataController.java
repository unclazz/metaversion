package org.unclazz.metaversion.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.MVApplication;
import org.unclazz.metaversion.vo.MasterData;

@RestController
@RequestMapping(MVApplication.REST_API_PATH_PREFIX)
public class ApiMasterDataController {
	/**
	 * 各種マスターデータをまとめたオブジェクトを返す.
	 * 
	 * @param principal 認証情報
	 * @return オブジェクト
	 */
	@RequestMapping(value="/masterdata", method=RequestMethod.GET)
	public MasterData getOne(final Principal principal) {
		return MasterData.getInstance();
	}
}
