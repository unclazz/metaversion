package org.unclazz.metaversion.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.unclazz.metaversion.mapper.OnlineBatchErrorMapper;
import org.unclazz.metaversion.mapper.OnlineBatchLockMapper;
import org.unclazz.metaversion.mapper.OnlineBatchLogMapper;
import org.unclazz.metaversion.vo.BatchResult;
import org.unclazz.metaversion.vo.OnlineBatchExecution;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;

@Service
public class BatchExecutorService {
	public static interface OnlineBatchRunnable extends Runnable {
		void run();
		OnlineBatchProgram getProgram();
	}
	public static interface OnlineBatchRunnableFactory {
		void setProgram(OnlineBatchProgram program);
		void setArguments(Object... args);
		void setArguments(Collection<Object> args);
		void setUserDetails(MVUserDetails auth);
		OnlineBatchRunnable create();
	}
	public static abstract class OnlineBatchRunnableFactorySupport 
	implements OnlineBatchRunnableFactory {
		private OnlineBatchProgram program;
		private final List<Object> args = new ArrayList<Object>();
		private MVUserDetails auth;
		@Override
		public void setProgram(final OnlineBatchProgram program) {
			this.program = program;
		}
		protected OnlineBatchProgram getProgram() {
			return program;
		}
		@Override
		public void setArguments(final Object... args) {
			setArguments(Arrays.asList(args));
		}
		@Override
		public void setArguments(final Collection<Object> args) {
			this.args.clear();
			this.args.addAll(args);
		}
		protected List<Object> getArguments() {
			return args;
		}
		@Override
		public void setUserDetails(final MVUserDetails auth) {
			this.auth = auth;
		}
		protected MVUserDetails getUserDetails() {
			return auth;
		}
	}
	
	public static final class ProccessIsAlreadyRunning extends RuntimeException {
		private static final long serialVersionUID = -4507775346531693192L;
		private ProccessIsAlreadyRunning() {
			super("Another process is already running.");
		}
		private ProccessIsAlreadyRunning(final Throwable cause) {
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
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private OnlineBatchLockMapper onlineBatchLockMapper;
	@Autowired
	private OnlineBatchLogMapper onlineBatchLogMapper;
	@Autowired
	private OnlineBatchErrorMapper onlineBatchErrorMapper;

	public Paginated<OnlineBatchExecution> getOnlineBatchExecutionList(final Paging paging) {
		final List<OnlineBatchExecution> list = new LinkedList<OnlineBatchExecution>();
		for (final OnlineBatchProgram p : OnlineBatchProgram.values()) {
			final OnlineBatchLog log = onlineBatchLogMapper.selectLastOneByProgramId(p.getId());
			final OnlineBatchExecution exec = new OnlineBatchExecution();
			exec.setProgram(p);
			exec.setLastStatus(OnlineBatchStatus.valueOfStatusId(log.getStatusId()));
			exec.setLastLog(log);
			list.add(exec);
		}
		return Paginated.of(paging, list, OnlineBatchProgram.values().length);
	}
	
	public OnlineBatchLock getLastExecutionLock(final OnlineBatchProgram p) {
		return onlineBatchLockMapper.selectOneByProgramId(p.getId());
	}
	
	public BatchResult executeAsynchronously(final OnlineBatchRunnable runnable) {
		new Thread(runnable).start();
		return BatchResult.ofNowStarting(runnable.getProgram());
	}
	
	public OnlineBatchRunnable wrapRunnableWithLock(final OnlineBatchProgram program, 
			final Runnable runnable, final MVUserDetails auth) {
		return new OnlineBatchRunnable() {
			@Override
			public void run() {
				execute(program, runnable, auth);
			}
			@Override
			public OnlineBatchProgram getProgram() {
				return program;
			}
		};
	}
	
	private void execute(final OnlineBatchProgram program, 
			final Runnable runnable, final MVUserDetails auth) {
		
		logger.debug("起動対象バッチ： {}", program.getProgramName());
		logger.debug("ロック取得を試行");
		
		// 事前処理：ロックを取得・更新しつつバッチログを登録
		final LockIdAndLogId lockAndLog = onBatchStart(program, auth);
		
		logger.debug("ロック取得に成功");
		
		try {
			logger.debug("指定されたロジックを起動");
			
			// メイン処理
			runnable.run();
			
			logger.debug("ロジックが正常終了");
			logger.debug("ロックを解放");
			
			// 事後処理：バッチログとロックを更新
			onBatchEnded(lockAndLog, auth);
			
			logger.debug("事後処理も含め正常終了");
			
		} catch (final RuntimeException ex) {
			// 何はともあれログ出力
			logger.error("ロジックが異常終了");
			logger.debug("エラー情報をDBに登録： {}", ex);
			
			// online_batch_errorに1レコードINSERT
			final OnlineBatchError error = new OnlineBatchError();
			error.setId(onlineBatchErrorMapper.selectNextVal());
			error.setOnlineBatchLogId(lockAndLog.getLogId());
			error.setErrorName(ex.getClass().getCanonicalName());
			error.setErrorMessage(MVUtils.stackTraceToCharSequence(ex).toString());
			onlineBatchErrorMapper.insert(error, auth);
			
			logger.debug("ロックを解放");
			
			// 事後処理：バッチログとロックを更新
			onBatchAbended(lockAndLog, ex, auth);
			
			logger.debug("事後処理は正常終了");
			logger.debug("例外を再度スロー");
			
			// その上で例外は再スロー
			throw ex;
		}
	}
	
	@Transactional
	public LockIdAndLogId onBatchStart(final OnlineBatchProgram program, final MVUserDetails auth) {
		// online_batch_lockレコードを格納する変数を宣言
		final OnlineBatchLock lock;
		try {
			// SELECT FOR UPDATE NOWAITを実行
			lock = onlineBatchLockMapper.
					selectOneForLockByProgramId(program.getId());
			// 結果値がnull（＝レコード0件）なら他のプロセスが起動中
			if (lock == null) {
				// 例外スローで処理を終える
				throw new ProccessIsAlreadyRunning();
			}
		} catch (final RuntimeException ex) {
			// SELECT FOR UPDATE NOWAIT実行により例外がスローされた場合
			// ほぼ同時に他のプロセスが起動中ということなので例外スローで処理を終える
			throw new ProccessIsAlreadyRunning(ex);
		}
		
		// online_batch_logレコードを新規作成
		final OnlineBatchLog log = new OnlineBatchLog();
		log.setId(onlineBatchLogMapper.selectNextVal());
		log.setStartDate(new Date());
		log.setEndDate(null);
		log.setProgramId(program.getId());
		log.setStatusId(OnlineBatchStatus.RUNNING.getId());
		
		// online_batch_log -> online_batch_lockの順にDML実行
		onlineBatchLogMapper.insert(log, auth);
		onlineBatchLockMapper.updateForLock(lock.getId(), auth);
		
		// ロックIDとログIDをVOに詰めて呼び出し元に返す
		return new LockIdAndLogId(lock.getId(), log.getId());
	}
	
	@Transactional
	public void onBatchEnded(final LockIdAndLogId lockAndLog, final MVUserDetails auth) {
		try {
			// online_batch_logレコードを取得し終了日時やステータスを変更
			final OnlineBatchLog log = onlineBatchLogMapper.selectOneById(lockAndLog.getLogId());
			log.setEndDate(new Date());
			log.setStatusId(OnlineBatchStatus.ENDED.getId());
			
			// online_batch_log -> online_batch_lockの順にDML実行
			onlineBatchLogMapper.update(log, auth);
		} finally {
			onlineBatchLockMapper.updateForUnlock(lockAndLog.getLockId(), auth);
		}
	}
	
	@Transactional
	public void onBatchAbended(final LockIdAndLogId lockAndLog, final Throwable cause, final MVUserDetails auth) {
		try {
			// online_batch_errorに1レコードINSERT
			final OnlineBatchError error = new OnlineBatchError();
			error.setId(onlineBatchErrorMapper.selectNextVal());
			error.setOnlineBatchLogId(lockAndLog.getLogId());
			error.setErrorName(cause.getClass().getCanonicalName());
			error.setErrorMessage(MVUtils.stackTraceToCharSequence(cause).toString());
			onlineBatchErrorMapper.insert(error, auth);
			
			// online_batch_logレコードを取得し終了日時やステータスを変更
			final OnlineBatchLog log = onlineBatchLogMapper.selectOneById(lockAndLog.getLogId());
			log.setEndDate(new Date());
			log.setStatusId(OnlineBatchStatus.ABENDED.getId());
			
			// online_batch_log -> online_batch_lockの順にDML実行
			onlineBatchLogMapper.update(log, auth);
		} finally {
			onlineBatchLockMapper.updateForUnlock(lockAndLog.getLockId(), auth);
		}
	}
}
