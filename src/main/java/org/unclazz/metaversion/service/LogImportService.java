package org.unclazz.metaversion.service;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unclazz.metaversion.MVProperties;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.MVUtils;
import org.unclazz.metaversion.entity.OnlineBatchError;
import org.unclazz.metaversion.entity.OnlineBatchLock;
import org.unclazz.metaversion.entity.OnlineBatchLog;
import org.unclazz.metaversion.entity.OnlineBatchProgram;
import org.unclazz.metaversion.entity.OnlineBatchStatus;
import org.unclazz.metaversion.entity.SvnCommit;
import org.unclazz.metaversion.entity.SvnCommitPath;
import org.unclazz.metaversion.entity.SvnRepository;
import org.unclazz.metaversion.mapper.OnlineBatchErrorMapper;
import org.unclazz.metaversion.mapper.OnlineBatchLockMapper;
import org.unclazz.metaversion.mapper.OnlineBatchLogMapper;
import org.unclazz.metaversion.mapper.SvnCommitMapper;
import org.unclazz.metaversion.mapper.SvnCommitPathMapper;
import org.unclazz.metaversion.mapper.SvnRepositoryMapper;
import org.unclazz.metaversion.service.SvnService.RepositoryRootAndHeadRevision;
import org.unclazz.metaversion.service.SvnService.SvnCommitAndItsPathList;

@Service
public class LogImportService {
	@Autowired
	private Logger logger;
	@Autowired
	private SvnRepositoryMapper svnRepositoryMapper;
	@Autowired
	private SvnCommitMapper svnCommitMapper;
	@Autowired
	private SvnCommitPathMapper svnCommitPathMapper;
	@Autowired
	private OnlineBatchLockMapper onlineBatchLockMapper;
	@Autowired
	private OnlineBatchLogMapper onlineBatchLogMapper;
	@Autowired
	private OnlineBatchErrorMapper onlineBatchErrorMapper;
	@Autowired
	private SvnService svnService;
	@Autowired
	private MVProperties props;

	public static final class LogImportAlreadyRunning extends RuntimeException {
		private static final long serialVersionUID = -4507775346531693192L;
		private LogImportAlreadyRunning() {
			super("Another process is already running.");
		}
		private LogImportAlreadyRunning(final Throwable cause) {
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
	public static final class SvnRepositoryPathInfo {
		private final String baseUrlPathComponent;
		private final Pattern compiledTrunkPathPattern;
		private final Pattern compliedBranchPathPattern;
		private SvnRepositoryPathInfo(final SvnRepository repository, final String rootUrl) {
			// リビジョンのベースURLからパス部分を切り出す
			if (!repository.getBaseUrl().startsWith(rootUrl)) {
				throw MVUtils.illegalArgument("Invalid base url. "
						+ "The url does not start with repository's root url"
						+ "(base url=%s, root url=%s).", repository.getBaseUrl(), rootUrl);
			}
			baseUrlPathComponent = repository.getBaseUrl().substring(rootUrl.length());
			// trunkパス部分にマッチする正規表現パターンを初期化
			compiledTrunkPathPattern = Pattern.compile(repository.getTrunkPathPattern());
			// branchパス部分にマッチする正規表現パターンを初期化
			compliedBranchPathPattern = Pattern.compile(repository.getBranchPathPattern());
		}
		public final String getBaseUrlPathComponent() {
			return baseUrlPathComponent;
		}
		public final Pattern getCompiledTrunkPathPattern() {
			return compiledTrunkPathPattern;
		}
		public final Pattern getCompliedBranchPathPattern() {
			return compliedBranchPathPattern;
		}
	}
	
	public static final class RevisionRange {
		private final int start;
		private final int end;
		private RevisionRange(final int start, final int end) {
			this.start = start;
			this.end = end;
		}
		public int getStart() {
			return start;
		}
		public int getEnd() {
			return end;
		}
		public int getEndExclusive() {
			return end + 1;
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
	
	public void doLogImport(final int repositoryId, final MVUserDetails auth) {
		// 事前処理：online_batch_lockの行ロックを取得したあとonline_batch_logに1行INSERT
		final LockIdAndLogId lockAndLog = doLogImportStart(auth);
		
		// メイン処理の状態・結果を示すOnlineBatchStatusのための変数
		OnlineBatchStatus status = OnlineBatchStatus.ABENDED;
		try {
			// メイン処理：svn logコマンドを実行して結果得られたデータをsvn_commitテーブルほかにINSERT
			doLogImportMain(repositoryId, auth);
			// この行が実行されたということは処理は正常終了したということ
			status = OnlineBatchStatus.ENDED;
		} catch (final RuntimeException ex) {
			// この行が実行されたということは処理は異常終了したということ
			// 何はともあれログ出力
			logger.error("Error has occurred at LogImportService.doLogImport.", ex);
			// online_batch_errorに1レコードINSERT
			final OnlineBatchError error = new OnlineBatchError();
			error.setId(onlineBatchErrorMapper.selectNextVal());
			error.setOnlineBatchLogId(lockAndLog.getLogId());
			error.setErrorName(ex.getClass().getCanonicalName());
			error.setErrorMessage(MVUtils.stackTraceToCharSequence(ex).toString());
			onlineBatchErrorMapper.insert(error, auth);
			
			throw ex;
		} finally {
			// 事後処理：online_batch_log -> online_batch_lockの順でUPDATE
			doImportEnd(lockAndLog, status, auth);
		}
	}
	
	public void doLogImportMain(int repositoryId, final MVUserDetails auth) {
		final SvnRepository repository = svnRepositoryMapper.selectOneById(repositoryId);
		
		// インポート済みのリビジョン番号を取得
		final int maxRevision = repository.getMaxRevision();
		 
		// SVNリポジトリ側のルートURLと最新リビジョンを取得
		// ＊数秒を要する可能性あり
		final RepositoryRootAndHeadRevision rootAndHead = svnService.getRepositoryRootAndHeadRevision(repository);
		final SvnRepositoryPathInfo pathInfo = new SvnRepositoryPathInfo(repository, rootAndHead.getRepositoryRootUrl());
		
		// 1トランザクションで取込みを行うリビジョンの単位（範囲）
		// ＊500リビジョンあたり10秒前後かかる可能性あり
		final int increment = props.getLogimportRevisionRange();
		
		// インポート済みリビジョン+1を開始リビジョン、HEADリビジョンを終了リビジョンとして
		// RevisionRangeリストを生成して、それを用いてループ処理を行う
		for (final RevisionRange range : makeRevisionRangeList(maxRevision + 1, rootAndHead.getHeadRevision(), increment)) {
			// RevisionRangeで示されるリビジョン範囲ごとにsvn logを実行して結果をDBに登録する
			doLogImportForRevisionRange(repository, pathInfo, range, auth);
		}
	}
	
	@Transactional
	public void doLogImportForRevisionRange(final SvnRepository repository, 
			final SvnRepositoryPathInfo pathInfo, final RevisionRange range, final MVUserDetails auth) {

		logger.info("取込み対象のベースパス（≠リポジトリルート）： {}", pathInfo.getBaseUrlPathComponent());
		logger.info("trunk部分パスの正規表現パターン： {} ", pathInfo.getCompiledTrunkPathPattern());
		logger.info("branches部分パスの正規表現パターン： {}", pathInfo.getCompliedBranchPathPattern());
		
		// SVNKitを通じsvn log -r <start>:<end> -v <url>コマンド実行
		final List<SvnCommitAndItsPathList> commitAndPathListList = svnService.
				getCommitAndItsPathList(repository, range.getStart(), range.getEnd());
		
		// svn logエントリ中の最大リビジョン
		final MaxRevision maxRevision = new MaxRevision(range.getStart());
		
		// svn logエントリ情報ごとのループ
		for (final SvnCommitAndItsPathList commitAndPathList : commitAndPathListList) {
			// svn logエントリのリビジョンとmaxRevision（＝これまで登場したリビジョンの最大）とを比較
			// より大きい方をmaxRevisionに保持させる
			final int currentRevision = commitAndPathList.getRevision();
			maxRevision.trySetNewValue(currentRevision);
			
			// svn logエントリ情報からsvn_commitレコードを作成
			final SvnCommit commit = new SvnCommit();
			commit.setId(svnCommitMapper.selectNextVal());
			commit.setCommitMessage(commitAndPathList.getCommitMessage());
			commit.setCommitDate(commitAndPathList.getCommitDate());
			commit.setCommitterName(commitAndPathList.getCommitterName());
			commit.setRevision(currentRevision);
			commit.setSvnRepositoryId(repository.getId());
			svnCommitMapper.insert(commit, auth);
			
			// svn logエントリのパス情報ごとのループ
			for (final SvnCommitPath path : commitAndPathList.getPathList()) {
				logger.info("URL正規化まえ： {} ", path.getPath());
				
				// SVNから返されたパスがアプリのリポジトリ設定にあるURLのベースパスで始まるのかチェック
				if (!path.getPath().startsWith(pathInfo.getBaseUrlPathComponent())) {
					// 結果NGの場合はインポート対象でないのでスキップ
					continue;
				}
				
				// SVNから返されたパスからアプリのリポジトリ設定にあるURLのベースパスを除去
				final CharSequence pathAfterBaseUrl = new StringBuilder(path.getPath())
						.subSequence(pathInfo.getBaseUrlPathComponent().length(), path.getPath().length());

				// ログ：URL正規化の結果
				logger.info("URL正規化あと リポジトリベースURL部分の除去： {} ", pathAfterBaseUrl);
				
				// trunkやbranchの部分パスを除去した正規化済みURLが格納される変数
				final CharSequence normalizedUrl;
				
				// trunkのパスに該当するかチェック
				final Matcher trunkMatcher = pathInfo.getCompiledTrunkPathPattern().matcher(pathAfterBaseUrl);
				if (trunkMatcher.lookingAt()) {
					// 該当する場合
					// trunkの部分パスを除去する
					normalizedUrl = pathAfterBaseUrl.subSequence(trunkMatcher.end(), pathAfterBaseUrl.length());
				} else {
					// 該当しない場合
					// branchのパスに該当するかチェック
					final Matcher branchMatcher = pathInfo.getCompliedBranchPathPattern().matcher(pathAfterBaseUrl);
					if (branchMatcher.lookingAt()) {
						// 該当する場合
						// branchの部分パスを除去する
						normalizedUrl = pathAfterBaseUrl.subSequence(branchMatcher.end(), pathAfterBaseUrl.length());
					} else {
						// 該当しない場合
						// インポート対象でないのでスキップ
						continue;
					}
				}
				
				logger.info("URL正規化あと trunk/branches部分パスの除去: {} ", normalizedUrl);
				
				// 念のため想定外の内容でないかチェック
				if (normalizedUrl.length() < 2 || normalizedUrl.charAt(0) != '/') {
					continue;
				}
				
				// svn logエントリのパス情報からsvn_commit_pathレコードを作成
				path.setId(svnCommitPathMapper.selectNextVal());
				path.setSvnCommitId(commit.getId());
				path.setPath(normalizedUrl.toString());
				svnCommitPathMapper.insert(path, auth);
			}
		}
		
		// Maxリビジョンでsvn_repositoryをUPDATE
		repository.setMaxRevision(maxRevision.getValue());
		svnRepositoryMapper.update(repository, auth);
	}
	
	@Transactional
	public LockIdAndLogId doLogImportStart(final MVUserDetails auth) {
		// online_batch_lockレコードを格納する変数を宣言
		final OnlineBatchLock lock;
		try {
			// SELECT FOR UPDATE NOWAITを実行
			lock = onlineBatchLockMapper.
					selectOneForUpdateNowaitByProgramId(OnlineBatchProgram.LOG_IMPORT.getId(), false, auth);
			// 結果値がnull（＝レコード0件）なら他のプロセスが起動中
			if (lock == null) {
				// 例外スローで処理を終える
				throw new LogImportAlreadyRunning();
			}
		} catch (final RuntimeException ex) {
			// SELECT FOR UPDATE NOWAIT実行により例外がスローされた場合
			// ほぼ同時に他のプロセスが起動中ということなので例外スローで処理を終える
			throw new LogImportAlreadyRunning(ex);
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
	
	public List<RevisionRange> makeRevisionRangeList(final int start, final int end, final int size) {
		// リビジョン番号としてまた増分としていずれも1より小さい数値はNG
		if (start < 1 || end < 1 || size < 1) {
			throw new IllegalArgumentException();
		}
		// 開始リビジョンのほうが大きい場合
		if (start > end) {
			// 空のリストを返すだけで処理を終える
			return Collections.emptyList();
		}
		// 続くfor文のために排他の上限値を定義
		final int endExclusive = end + 1;
		// 結果値となるリストを初期化
		final List<RevisionRange> result = new LinkedList<LogImportService.RevisionRange>();
		// 指定された増分を用いて繰り返しRevisionRangeを作成
		for (int i = start; i < endExclusive; i += size) {
			// 指定された増分を用いて終了リビジョンを単純計算
			final int iPlusSizeMinus1 = i + size - 1;
			// リストにRevisionRangeを追加
			// 単純計算した個別の終了リビジョンが全体の終了リビジョンを超える場合は後者を採用
			result.add(new RevisionRange(i, iPlusSizeMinus1 < end ? iPlusSizeMinus1 : end));
		}
		// 結果を呼び出し元に返す
		return result;
	}
	
	@Transactional
	public void doImportEnd(final LockIdAndLogId lockAndLog, final OnlineBatchStatus status, final MVUserDetails auth) {
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
