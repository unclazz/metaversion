package org.unclazz.metaversion.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unclazz.metaversion.MVProperties;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.MVUtils;
import org.unclazz.metaversion.entity.OnlineBatchLock;
import org.unclazz.metaversion.entity.OnlineBatchProgram;
import org.unclazz.metaversion.entity.SvnCommit;
import org.unclazz.metaversion.entity.SvnCommitPath;
import org.unclazz.metaversion.entity.SvnCommitPathWithRawInfo;
import org.unclazz.metaversion.entity.SvnRepository;
import org.unclazz.metaversion.mapper.SvnCommitMapper;
import org.unclazz.metaversion.mapper.SvnCommitPathMapper;
import org.unclazz.metaversion.mapper.SvnRepositoryMapper;
import org.unclazz.metaversion.service.SvnCommandService.SvnCommitAndItsPathList;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.MaxRevision;
import org.unclazz.metaversion.vo.OrderByClause;
import org.unclazz.metaversion.vo.RevisionRange;
import org.unclazz.metaversion.vo.SvnRepositoryInfo;
import org.unclazz.metaversion.vo.SvnRepositoryPathInfo;

@Service
public class LogImporterService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SvnRepositoryMapper svnRepositoryMapper;
	@Autowired
	private SvnCommitMapper svnCommitMapper;
	@Autowired
	private SvnCommitPathMapper svnCommitPathMapper;
	@Autowired
	private SvnCommandService svnService;
	@Autowired
	private MVProperties props;
	@Autowired
	private BatchExecutorService executorService;
	@Autowired
	private SystemBootLogService bootLogService;
	
	public void doLogImportAsynchronously(final MVUserDetails auth) {
		final OnlineBatchLock lock = executorService.getLastExecutionLock(OnlineBatchProgram.LOG_IMPORTER);
		final Date nowBootDate = bootLogService.getSystemBootDate();
		
		logger.info("SVNログインポート（非同期）を開始");
		logger.info("ロック状態： {}", lock.isLocked());
		logger.info("ロック時システムブート日時： {}", lock.getSystemBootDate());
		logger.info("現在時点システムブート日時： {}", nowBootDate);
		
		if (lock.isLocked() && lock.getSystemBootDate().compareTo(nowBootDate) >= 0) {
			logger.info("SVNログインポート（非同期）を中止");
			return;
		}
		final Date lastExecDate = lock.getLastUnlockDate();
		final Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE, -60);
		final Date xMinutesBefore = cal.getTime();

		logger.info("最終バッチ実行日時： {}", lastExecDate);
		logger.info("バッチ起動閾値日時： {}", xMinutesBefore);

		if (lastExecDate.compareTo(xMinutesBefore) > 0) {
			logger.info("SVNログインポート（非同期）を中止");
			return;
		}
		
		final OrderByClause orderBy = OrderByClause.noParticularOrder();
		final LimitOffsetClause limitOffset = LimitOffsetClause.ALL;
		final List<SvnRepository> list = svnRepositoryMapper.selectAll(orderBy, limitOffset);
		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				logger.info("対象リポジトリ数： {}", list.size());
				for (final SvnRepository r : list) {
					logger.info("対象リポジトリ： {}({})", r.getName(), r.getId());
					try {
						doLogImportMain(r.getId(), auth);
					} catch (final Exception e) {
						logger.info("処理中の例外スロー： {}", r.getName(), r.getId(), e);
					}
				}
				logger.info("SVNログインポート（非同期）を終了");
			}
		};
		new Thread(executorService.
				wrapRunnableWithLock(OnlineBatchProgram.LOG_IMPORTER,
				runnable, auth))
		.start();
	}
	
	public void doLogImportMain(int repositoryId, final MVUserDetails auth) {
		final SvnRepository repository = svnRepositoryMapper.selectOneById(repositoryId);
		
		// インポート済みのリビジョン番号を取得
		final int maxRevision = repository.getMaxRevision();
		
		// SVNリポジトリ側のルートURLと最新リビジョンを取得
		// ＊数秒を要する可能性あり
		final SvnRepositoryInfo svnInfo = svnService.getRepositoryInfo(repository, 2);
		final SvnRepositoryPathInfo svnRepositoryPathInfo = SvnRepositoryPathInfo.composedOf(svnInfo, repository);
		
		// インポート済みのリビジョン番号とSVNリポジトリ本体の最新リビジョン番号を比較
		if (maxRevision >= svnInfo.getHeadRevision()) {
			// インポート済みがすでに最新ならここで処理を終える
			return;
		}
		
		// ベースパス配下のSVNにおける最初（最古）のリビジョンを取得
		final int firstRevision = svnService.getFirstRevision(repository, 2);
		// 開始リビジョンの決定
		// ＊無駄を避けるためインポート済み番号よりも最初（最古）のリビジョンが大きい場合は後者を採用する
		final int startRevision = (firstRevision > maxRevision ? firstRevision : maxRevision) + 1;
		
		// 1トランザクションで取込みを行うリビジョンの単位（範囲）
		// ＊500リビジョンあたり10秒前後かかる可能性あり
		final List<RevisionRange> rangeList = RevisionRange
				.ofBetween(startRevision, svnInfo.getHeadRevision())
				.withStep(props.getLogimportRevisionRange());
		
		// インポート済みリビジョン+1を開始リビジョン、HEADリビジョンを終了リビジョンとして
		// RevisionRangeリストを生成して、それを用いてループ処理を行う
		for (final RevisionRange range : rangeList) {
			// RevisionRangeで示されるリビジョン範囲ごとにsvn logを実行して結果をDBに登録する
			doLogImportForRevisionRange(repository, svnRepositoryPathInfo, range, auth);
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
				getCommitAndItsPathList(repository, range, 2);
		
		// svn logエントリ中の最大リビジョン
		final MaxRevision maxRevision = MaxRevision.startsWith(range.getStart());
		
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
			commit.setRepositoryId(repository.getId());
			svnCommitMapper.insert(commit, auth);
			
			// svn logエントリのパス情報ごとのループ
			for (final SvnCommitPath path : commitAndPathList.getPathList()) {
				logger.info("URL正規化まえ： {} ", path.getPath());
				
				// SVNから返されたパスがアプリのリポジトリ設定にあるURLのベースパスで始まるのかチェック
				if (!path.getPath().startsWith(pathInfo.getBaseUrlPathComponent())) {
					// 結果NGの場合はインポート対象でないのでスキップ
					continue;
				}
				
				// INSERT用のVOを初期化
				final SvnCommitPathWithRawInfo rec = new SvnCommitPathWithRawInfo();
				// 新しいVOにchangeTypeIdをコピー
				// ＊id、commitId、path、rawPath、basePathSegment、branchPathSegmentはこのあと設定
				rec.setChangeTypeId(path.getChangeTypeId());
				
				// 新しいVOにrawPathとbasePathSegmentを設定
				rec.setRawPath(path.getPath());
				rec.setBasePathSegment(pathInfo.getBaseUrlPathComponent());
				
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
					
					// INSERT用VOにbranchPathSegmentを設定
					rec.setBranchPathSegment(MVUtils.slashNormalize(trunkMatcher.group()).toString());
				} else {
					// 該当しない場合
					// branchのパスに該当するかチェック
					final Matcher branchMatcher = pathInfo.getCompliedBranchPathPattern().matcher(pathAfterBaseUrl);
					if (branchMatcher.lookingAt()) {
						// 該当する場合
						// branchの部分パスを除去する
						normalizedUrl = pathAfterBaseUrl.subSequence(branchMatcher.end(), pathAfterBaseUrl.length());
						
						// INSERT用VOにbranchPathSegmentを設定
						rec.setBranchPathSegment(MVUtils.slashNormalize(branchMatcher.group()).toString());
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
				
				// INSERT用VOにid、commitId、pathを設定
				rec.setId(svnCommitPathMapper.selectNextVal());
				rec.setCommitId(commit.getId());
				rec.setPath(MVUtils.slashNormalize(normalizedUrl).toString());
				svnCommitPathMapper.insert(rec, auth);
			}
		}
		
		// Maxリビジョンでsvn_repositoryをUPDATE
		repository.setMaxRevision(maxRevision.getValue());
		svnRepositoryMapper.update(repository, auth);
	}
}
