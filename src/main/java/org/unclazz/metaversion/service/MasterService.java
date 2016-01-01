package org.unclazz.metaversion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unclazz.metaversion.MVProperties;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.ChangeType;
import org.unclazz.metaversion.entity.OnlineBatchProgram;
import org.unclazz.metaversion.entity.OnlineBatchStatus;
import org.unclazz.metaversion.entity.User;
import org.unclazz.metaversion.mapper.ChangeTypeMapper;
import org.unclazz.metaversion.mapper.OnlineBatchProgramMapper;
import org.unclazz.metaversion.mapper.OnlineBatchStatusMapper;
import org.unclazz.metaversion.mapper.UserMapper;

@Service
public class MasterService {
	public static final class ApplicationMayBeAlreadyInitialized extends RuntimeException {
		private static final long serialVersionUID = -512290824405502812L;
		private ApplicationMayBeAlreadyInitialized(Throwable cause) {
			super(cause);
		}
	}
	
	@Autowired
	private ChangeTypeMapper chageTypeMapper;
	@Autowired
	private OnlineBatchProgramMapper onlineBatchProgramMapper;
	@Autowired
	private OnlineBatchStatusMapper onlineBatchStatusMapper;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private MVProperties props;
	@Autowired
	private PasswordEncoder encorder;
	
	/**
	 * マスタデータを初期化する.
	 * @param auth 認証済みユーザ情報
	 * @throws ApplicationMayBeAlreadyInitialized すでに初期化済みと思われる場合
	 */
	@Transactional
	public void initializeMaster() {
		final User defaultAdmin = new User();
		defaultAdmin.setId(0);
		defaultAdmin.setName(props.getDefaultAdminName());
		defaultAdmin.setPassword(encorder.encode(new StringBuilder().append(props.getDefaultAdminPassword())));
		defaultAdmin.setAdmin(true);
		final MVUserDetails auth = MVUserDetails.of(defaultAdmin);
		
		try {
			userMapper.insert(defaultAdmin, auth);
		} catch (final RuntimeException e) {
			throw new ApplicationMayBeAlreadyInitialized(e);
		}
		
		
		for (final ChangeType value : ChangeType.values()) {
			chageTypeMapper.insert(value);
		}
		for (final OnlineBatchProgram value : OnlineBatchProgram.values()) {
			onlineBatchProgramMapper.insert(value);
		}
		for (final OnlineBatchStatus value : OnlineBatchStatus.values()) {
			onlineBatchStatusMapper.insert(value);
		}
		
	}
}
