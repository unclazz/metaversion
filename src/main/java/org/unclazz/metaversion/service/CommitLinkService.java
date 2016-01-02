package org.unclazz.metaversion.service;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.MVUtils;
import org.unclazz.metaversion.entity.OnlineBatchError;
import org.unclazz.metaversion.entity.OnlineBatchLock;
import org.unclazz.metaversion.entity.OnlineBatchLog;
import org.unclazz.metaversion.entity.OnlineBatchProgram;
import org.unclazz.metaversion.entity.OnlineBatchStatus;
import org.unclazz.metaversion.entity.Project;
import org.unclazz.metaversion.entity.ProjectSvnRepository;
import org.unclazz.metaversion.entity.SvnCommit;
import org.unclazz.metaversion.mapper.OnlineBatchErrorMapper;
import org.unclazz.metaversion.mapper.OnlineBatchLockMapper;
import org.unclazz.metaversion.mapper.OnlineBatchLogMapper;
import org.unclazz.metaversion.mapper.ProjectMapper;
import org.unclazz.metaversion.mapper.ProjectSvnCommitMapper;
import org.unclazz.metaversion.mapper.ProjectSvnRepositoryMapper;
import org.unclazz.metaversion.mapper.SvnCommitMapper;

@Service
public class CommitLinkService {
	public static final class CommitLinkAlreadyRunning extends RuntimeException {
		private static final long serialVersionUID = -4507775346531693192L;
		private CommitLinkAlreadyRunning() {
			super("Another process is already running.");
		}
		private CommitLinkAlreadyRunning(final Throwable cause) {
			super("Another process is already running.", cause);
		}
	}
	
	public static final class LockIdAndLogId {
		private final int lockId;
		private final int logId;
		private LockIdAndLogId(final int lockId, final int logId) {
			this.lockId = lockId;
			this.logId = logId;
		}
		public final int getLockId() {
			return lockId;
		}
		public final int getLogId() {
			return logId;
		}
	}
	
	public static final class MaxRevision {
		private int max;
		private MaxRevision(final int initial) {
			max = initial;
		}
		public boolean trySetNewValue(final int value) {
			if (max < value) {
				max = value;
				return true;
			} else {
				return false;
			}
		}
		public int getValue() {
			return max;
		}
	}
	
	@Autowired
	private Logger logger;
	@Autowired
	private ProjectMapper projectMapper;
	@Autowired
	private OnlineBatchLockMapper onlineBatchLockMapper;
	@Autowired
	private OnlineBatchLogMapper onlineBatchLogMapper;
	@Autowired
	private OnlineBatchErrorMapper onlineBatchErrorMapper;
	@Autowired
	private SvnCommitMapper svnCommitMapper;
	@Autowired
	private ProjectSvnRepositoryMapper projectSvnRepositoryMapper;
	@Autowired
	private ProjectSvnCommitMapper projectSvnCommitMapper;
	
	public void doCommitLink(final int projectId, final MVUserDetails auth) {
		// 事前処理：ロックを取得・更新しつつバッチログを登録
		final LockIdAndLogId lockAndLog = doCommitLinkStart(auth);
		
		// メイン処理の状態・結果を示すOnlineBatchStatusのための変数
		OnlineBatchStatus status = OnlineBatchStatus.ABENDED;
		try {
			// メイン処理
			doCommitLinkMain(projectId, auth);
			// この行が実行されたということは処理は正常終了したということ
			status = OnlineBatchStatus.ENDED;
		} catch (final RuntimeException ex) {
			// この行が実行されたということは処理は異常終了したということ
			// 何はともあれログ出力
			logger.error("Error has occurred at CommitLinkService.doCommitLink.", ex);
			// online_batch_errorに1レコードINSERT
			final OnlineBatchError error = new OnlineBatchError();
			error.setId(onlineBatchErrorMapper.selectNextVal());
			error.setOnlineBatchLogId(lockAndLog.getLogId());
			error.setErrorName(ex.getClass().getCanonicalName());
			error.setErrorMessage(MVUtils.stackTraceToCharSequence(ex).toString());
			onlineBatchErrorMapper.insert(error, auth);
			
			throw ex;
		} finally {
			// 事後処理：バッチログとロックを更新
			doCommitLinkEnd(lockAndLog, status, auth);
		}
		
	}

	@Transactional
	public void doCommitLinkMain(final int projectId, final MVUserDetails auth) {
		final Project proj = projectMapper.selectOneById(projectId);
		if (proj == null) {
			throw MVUtils.illegalArgument("Unknown project(id=%s).", projectId);
		}
		
		final Pattern compiledPattern = Pattern.compile(proj.getCommitSignPattern());
		projectSvnRepositoryMapper.insertMissingLink(auth);
		
		final List<ProjectSvnRepository> obsoletedList = projectSvnRepositoryMapper.
				selectObsoletedRecordByProjectId(projectId);
		
		for (final ProjectSvnRepository obsoleted : obsoletedList) {
			final List<SvnCommit> commitList = svnCommitMapper.
					selectForMatchingByProjectIdAndRepositoryId(projectId, obsoleted.getSvnRepositoryId());
			final MaxRevision maxRevision = new MaxRevision(obsoleted.getLastRevision());
			
			for (final SvnCommit commit : commitList) {
				final Matcher matcher = compiledPattern.matcher(commit.getCommitMessage());
				if (!matcher.find()) {
					continue;
				}
				projectSvnCommitMapper.insert(projectId, commit.getId(), auth);
				maxRevision.trySetNewValue(commit.getRevision());
			}
			
			obsoleted.setLastRevision(maxRevision.getValue());
			projectSvnRepositoryMapper.update(obsoleted, auth);
		}
	}
	
	@Transactional
	public LockIdAndLogId doCommitLinkStart(final MVUserDetails auth) {
		// online_batch_lockレコードを格納する変数を宣言
		final OnlineBatchLock lock;
		try {
			// SELECT FOR UPDATE NOWAITを実行
			lock = onlineBatchLockMapper.
					selectOneForUpdateNowaitByProgramId(OnlineBatchProgram.LOG_IMPORT.getId(), false, auth);
			// 結果値がnull（＝レコード0件）なら他のプロセスが起動中
			if (lock == null) {
				// 例外スローで処理を終える
				throw new CommitLinkAlreadyRunning();
			}
		} catch (final RuntimeException ex) {
			// SELECT FOR UPDATE NOWAIT実行により例外がスローされた場合
			// ほぼ同時に他のプロセスが起動中ということなので例外スローで処理を終える
			throw new CommitLinkAlreadyRunning(ex);
		}
		
		// online_batch_logレコードを新規作成
		final OnlineBatchLog log = new OnlineBatchLog();
		log.setId(onlineBatchLogMapper.selectNextVal());
		log.setStartDate(new Date());
		log.setEndDate(null);
		log.setProgramId(OnlineBatchProgram.LOG_IMPORT.getId());
		log.setStatusId(OnlineBatchStatus.RUNNING.getId());
		
		// online_batch_log -> online_batch_lockの順にDML実行
		onlineBatchLogMapper.insert(log, auth);
		onlineBatchLockMapper.updateForLock(lock.getId(), auth);
		
		// ロックIDとログIDをVOに詰めて呼び出し元に返す
		return new LockIdAndLogId(lock.getId(), log.getId());
	}
	
	@Transactional
	public void doCommitLinkEnd(final LockIdAndLogId lockAndLog, final OnlineBatchStatus status, final MVUserDetails auth) {
		try {
			// online_batch_logレコードを取得し終了日時やステータスを変更
			final OnlineBatchLog log = onlineBatchLogMapper.selectOneById(lockAndLog.getLogId());
			log.setEndDate(new Date());
			log.setStatusId(status.getId());
			
			// online_batch_log -> online_batch_lockの順にDML実行
			onlineBatchLogMapper.update(log, auth);
		} finally {
			onlineBatchLockMapper.updateForUnlock(lockAndLog.getLockId(), auth);
		}
	}
}
