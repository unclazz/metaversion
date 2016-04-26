package org.unclazz.metaversion.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unclazz.metaversion.MVProperties;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.ChangeType;
import org.unclazz.metaversion.entity.OnlineBatchLock;
import org.unclazz.metaversion.entity.OnlineBatchLog;
import org.unclazz.metaversion.entity.OnlineBatchProgram;
import org.unclazz.metaversion.entity.OnlineBatchStatus;
import org.unclazz.metaversion.entity.User;
import org.unclazz.metaversion.mapper.ChangeTypeMapper;
import org.unclazz.metaversion.mapper.OnlineBatchLockMapper;
import org.unclazz.metaversion.mapper.OnlineBatchLogMapper;
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
	private OnlineBatchLockMapper onlineBatchLockMapper;
	@Autowired
	private OnlineBatchLogMapper onlineBatchLogMapper;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private MVProperties props;
	@Autowired
	private PasswordEncoder encorder;
	@Autowired
	private SystemBootLogService bootLogService;
	
	/**
	 * マスタデータを初期化する.
	 * @param auth 認証済みユーザ情報
	 * @throws ApplicationMayBeAlreadyInitialized すでに初期化済みと思われる場合
	 */
	@Transactional
	public void initializeMaster() {
		final Date now = new Date();
		final User defaultAdmin = new User();
		defaultAdmin.setId(0);
		defaultAdmin.setName(props.getDefaultAdminName());
		defaultAdmin.setEncodedPassword(encorder.encode(new StringBuilder().append(props.getDefaultAdminPassword())));
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
		for (final OnlineBatchStatus value : OnlineBatchStatus.values()) {
			onlineBatchStatusMapper.insert(value);
		}
		for (final OnlineBatchProgram value : OnlineBatchProgram.values()) {
			onlineBatchProgramMapper.insert(value);
			
			final OnlineBatchLock lock = new OnlineBatchLock();
			lock.setId(onlineBatchLockMapper.selectNextVal());
			lock.setLastLockDate(now);
			lock.setLastUnlockDate(now);
			lock.setLocked(false);
			lock.setProgramId(value.getId());
			lock.setSystemBootDate(bootLogService.getSystemBootDate());
			onlineBatchLockMapper.insert(lock);
			
			final OnlineBatchLog log = new OnlineBatchLog();
			log.setId(onlineBatchLogMapper.selectNextVal());
			log.setProgramId(value.getId());
			log.setStartDate(now);
			log.setEndDate(now);
			log.setStatusId(OnlineBatchStatus.ENDED.getId());
			onlineBatchLogMapper.insert(log, auth);
		}
	}
}
