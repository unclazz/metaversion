package org.unclazz.metaversion.service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.util.SVNEncodingUtil;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.unclazz.metaversion.entity.ChangeType;
import org.unclazz.metaversion.entity.SvnCommit;
import org.unclazz.metaversion.entity.SvnCommitPath;
import org.unclazz.metaversion.entity.SvnRepository;

@Service
public class SvnService {
	@Autowired
	private Logger logger;
	
	public static final class SvnCommitAndItsPathList extends SvnCommit {
		private final List<SvnCommitPath> pathList = new LinkedList<SvnCommitPath>();
		public List<SvnCommitPath> getPathList() {
			return pathList;
		}
	}
	public static final class RepositoryRootAndHeadRevision {
		private final String repositoryRootUrl;
		private final int headRevision;
		private RepositoryRootAndHeadRevision(final String url, final int revision) {
			this.repositoryRootUrl = url;
			this.headRevision = revision;
		}
		public String getRepositoryRootUrl() {
			return repositoryRootUrl;
		}
		public int getHeadRevision() {
			return headRevision;
		}
	}
	public static final class SvnOperationFailed extends RuntimeException {
		private static final long serialVersionUID = -4807929824203444080L;
		private SvnOperationFailed(final String message, final Throwable cause) {
			super(message, cause);
		}
	}
	
	/**
	 * {@link SVNClientManager}を初期化して返す.
	 * @param repository リポジトリ情報
	 * @return {@link SVNClientManager}インスタンス
	 */
	private SVNClientManager getSVNClientManager(final SvnRepository repository) {
		final String username = repository.getUsername();
		final String password = repository.getPassword();
		final DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
		if (username == null || username.isEmpty()) {
			return SVNClientManager.newInstance(options);
		} else {
			return SVNClientManager.newInstance(options, username, password);
		}
	}
	
	/**
	 * URIエンコードされていない文字列から{@link SVNURL}を生成して返す.
	 * @param repository リポジトリ情報
	 * @return {@link SVNURL}インスタンス
	 * @throws SVNException SVNKitのAPIが例外をスローした場合
	 */
	private SVNURL getSVNURL(final SvnRepository repository) throws SVNException {
		return SVNURL.parseURIEncoded(SVNEncodingUtil. autoURIEncode(repository.getBaseUrl()));
	}
	
	/**
	 * {@code svn info <url>}コマンドを実行して得られたルートURLとHEADリビジョン番号を返す.
	 * メソッドの処理時間は、接続先のSVNサーバの応答時間とSVNKitのAPIの処理時間の合算値となり、
	 * HEADリビジョンの番号取得に数秒かかることがある。
	 * @param repository リポジトリ情報
	 * @return ルートURLとHEADリビジョン
	 */
	public RepositoryRootAndHeadRevision getRepositoryRootAndHeadRevision(SvnRepository repository) {
		final SVNClientManager manager = getSVNClientManager(repository);
		final SVNWCClient client = manager.getWCClient();
		try {
			final SVNInfo info = client.doInfo(getSVNURL(repository), SVNRevision.HEAD, SVNRevision.HEAD);
			return new RepositoryRootAndHeadRevision(
					info.getRepositoryRootURL().toDecodedString(),
					(int) info.getRevision().getNumber());
		} catch (final Exception e) {
			// SVNKitのAPIから例外がスローされた場合はラップして再スローする
			throw new SvnOperationFailed("'svn info' command failed.", e);
		}
	}
	
	/**
	 * {@code svn log -r <start>:<end> -v <url>}コマンドを実行して得られたコミット情報を返す.
	 * メソッドの処理時間は、接続先のSVNサーバの応答時間とSVNKitのAPIの処理時間の合算値となり、
	 * 500リビジョン分の処理に10秒前後かかることがある。
	 * @param repository リポジトリ情報
	 * @param startRevision 開始リビジョン
	 * @param endRevision 終了リビジョン
	 * @return コミットとコミットで変更されたリソースの情報
	 */
	public List<SvnCommitAndItsPathList> getCommitAndItsPathList(final SvnRepository repository,
			int startRevision, final int endRevision) {
		// VOを初期化
		final List<SvnCommitAndItsPathList> commitAndPathListList = new LinkedList<SvnCommitAndItsPathList>();
		// svn logコマンド用のクライアントを初期化
		final SVNClientManager manager = getSVNClientManager(repository);
		final SVNLogClient client = manager.getLogClient();
		try {
			// svn logコマンドを実行する
			client.doLog(getSVNURL(repository),
					/* paths= */ new String[0],
					/* pegRevision= */ SVNRevision.UNDEFINED,
					/* startRevision= */ SVNRevision.create(startRevision),
					/* endRevision= */ SVNRevision.create(endRevision),
					/* stopOnCopy= */ false,
					/* discoverChangedPaths= */ true,
					/* includeMergedRevisions= */ true,
					/* limit= */ endRevision + 1 - startRevision,
					/* revisionProperties */ new String[0],
					new ISVNLogEntryHandler() {
						@Override
						public void handleLogEntry(final SVNLogEntry logEntry) throws SVNException {
							logger.info("process a svn log entry: revision={} author={} date={} paths={}. ",
									logEntry.getRevision(), logEntry.getAuthor(), logEntry.getDate(),
									logEntry.getChangedPaths().size());
							if (logEntry.getDate() == null) {
								logger.info("last entry was skipped because its date property is null.");
								return;
							}
							
							// VOを初期化
							final SvnCommitAndItsPathList commitAndPathList = new SvnCommitAndItsPathList();
							// SVNKitは独自拡張したDate型を返すのでこれを純正のDate型に変換する
							// ＊こうしないとPostgreSQLのドライバでエラーが発生する
							final Date javaUtilDate = new Date();
							javaUtilDate.setTime(logEntry.getDate().getTime());
							// コミット情報を転写
							commitAndPathList.setCommitMessage(logEntry.getMessage());
							commitAndPathList.setCommitDate(javaUtilDate);
							commitAndPathList.setCommitterName(logEntry.getAuthor());
							commitAndPathList.setRevision((int) logEntry.getRevision());
							// コミットにより変更されたパスをループ処理
							for (final SVNLogEntryPath logEntryPath : logEntry.getChangedPaths().values()) {
								// VOを初期化
								final SvnCommitPath path = new SvnCommitPath();
								// パス情報を転写
								path.setChangeTypeId(ChangeType.valueOfCode(logEntryPath.getType()).getId());
								path.setPath(logEntryPath.getPath());
								// コミット情報のVOに追加
								commitAndPathList.getPathList().add(path);
							}
							// エンクロージング・スコープの結果リストにコミット情報のVOを追加
							commitAndPathListList.add(commitAndPathList);
						}
			});
			// 結果を呼び出し元に返す
			return commitAndPathListList;
		} catch (final Exception e) {
			// SVNKitのAPIから例外がスローされた場合はラップして再スローする
			throw new SvnOperationFailed("'svn log' command failed.", e);
		}
	}
}
