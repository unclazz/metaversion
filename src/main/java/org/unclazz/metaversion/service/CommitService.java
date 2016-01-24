package org.unclazz.metaversion.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.unclazz.metaversion.entity.ProjectChangedPath;
import org.unclazz.metaversion.entity.SvnCommit;
import org.unclazz.metaversion.entity.SvnCommitPath;
import org.unclazz.metaversion.entity.SvnCommitStats;
import org.unclazz.metaversion.entity.SvnCommitWithRepositoryInfo;
import org.unclazz.metaversion.mapper.SvnCommitMapper;
import org.unclazz.metaversion.mapper.SvnCommitPathMapper;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;
import org.unclazz.metaversion.vo.OrderByClause.Order;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;

@Service
public class CommitService {
	private static final Charset csvCharset = Charset.forName("Windows-31j");
	
	@Autowired
	private SvnCommitMapper svnCommitMapper;
	@Autowired
	private SvnCommitPathMapper svnCommitPathMapper;
	
	public Paginated<SvnCommit> getProjectUndeterminedCommitList(final int repositoryId, final Paging paging) {
		final OrderByClause orderBy = OrderByClause.of("revision", Order.DESC);
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(paging);
		return Paginated.of(paging,
				svnCommitMapper.selectUnlinkedByRepositoryId(repositoryId, orderBy, limitOffset),
				svnCommitMapper.selectUnlinkedCountByRepositoryId(repositoryId));
	}
	
	public Paginated<SvnCommit> getCommitListByRepositoryId(final int repositoryId, final Paging paging) {
		final OrderByClause orderBy = OrderByClause.of("revision", Order.DESC);
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(paging);

		// コミット情報を検索する
		return Paginated.of(paging, 
				svnCommitMapper.selectByRepositoryId(repositoryId, orderBy, limitOffset),
				svnCommitMapper.selectCountByRepositoryId(repositoryId));
		
	}
	
	public Paginated<SvnCommitStats> getCommitStatsListByRepositoryId(final int repositoryId, final Paging paging) {
		final OrderByClause orderBy = OrderByClause.of("revision", Order.DESC);
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(paging);

		// コミット情報を検索する
		return Paginated.of(paging, 
				svnCommitMapper.selectStatsByRepositoryId(repositoryId, orderBy, limitOffset),
				svnCommitMapper.selectStatsCountByRepositoryId(repositoryId));
		
	}
	
	public SvnCommitStats getCommitStatsByCommitId(final int commitId) {
		return svnCommitMapper.selectStatsOneByCommitId(commitId);
		
	}
	
	public Paginated<SvnCommitWithRepositoryInfo> getCommitListByProjectId(final int projectId, final Paging paging) {
		final OrderByClause orderBy = OrderByClause.of("revision", Order.DESC);
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(paging);

		// コミット情報を検索する
		return Paginated.of(paging, 
				svnCommitMapper.selectByProjectId(projectId, orderBy, limitOffset),
				svnCommitMapper.selectCountByProjectId(projectId));
		
	}
	
	public SvnCommitWithRepositoryInfo getCommitWithRepositoryInfoByCommitId(final int commitId) {
		return svnCommitMapper.selectWithRepositoryInfoById(commitId);
	}
	
	public SvnCommit getCommitById(final int commitId) {
		return svnCommitMapper.selectOneById(commitId);
	}
	
	public Paginated<ProjectChangedPath> getChangedPathListByProjectId(final int projectId, final Paging paging) {
		final OrderByClause orderBy = OrderByClause.of("path", Order.DESC);
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(paging);

		// コミット情報を検索する
		return Paginated.of(paging, 
				svnCommitPathMapper.selectByProjectId(projectId, orderBy, limitOffset),
				svnCommitPathMapper.selectCountByProjectId(projectId));
		
	}

	public Resource getProjectChangedPathCsvByProjectId(final int id) throws IOException {
		final OrderByClause orderBy = OrderByClause.of("repositoryName").and("path");
		final LimitOffsetClause limitOffset = LimitOffsetClause.ALL;
		
		// CSVファイルのコンテンツを一時的に格納するためバイト配列出力ストリームを初期化
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		// そのストリームをラップするかたちでライターを初期化
		final OutputStreamWriter osw = new OutputStreamWriter(os, csvCharset);
		// CSVフォーマットを定義
		final CSVFormat format = CSVFormat.EXCEL.withHeader(
				"REPOSITORY_ID",
				"REPOSITORY_NAME",
				"PATH",
				"COMMIT_COUNT",
				"MIN_REVISION",
				"MIN_COMMIT_DATE",
				"MAX_REVISION",
				"MAX_COMMIT_DATE");
		
		// CSVプリンタを初期化
		final CSVPrinter printer = format.print(osw);
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		for (final ProjectChangedPath rec : svnCommitPathMapper.selectByProjectId(id, orderBy, limitOffset)) {
			// CSVレコードを書き出す
			printer.printRecord(
					rec.getRepositoryId(),
					rec.getRepositoryName(),
					rec.getPath(),
					rec.getCommitCount(),
					rec.getMinRevision(),
					dateFormat.format(rec.getMinCommitDate()),
					rec.getMaxRevision(),
					dateFormat.format(rec.getMaxCommitDate()));
		}
		
		// プリンタをクローズ
		printer.close();
		
		// ストリームに書き込まれた情報を再度バイト配列に変換
		// Resourceインスタンスを初期化して返す
		return new ByteArrayResource(os.toByteArray());
	}
	public Paginated<SvnCommitPath> getChangedPathListByRepositoryIdAndCommitId(
			final int repositoryId, final int commitId, final Paging paging) {
		// ＊検索にはリポジトリIDは利用しないが概念的に親子関係（親＝リポジトリ、子＝コミット）にあるため
		// 念のため引数に取るようにしている。今後エンティティ構造に変更があった場合に役立つかもしれない。
		
		final OrderByClause orderBy = OrderByClause.of("path");
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(paging);
		
		// パス情報を検索する
		return Paginated.of(paging, 
				svnCommitPathMapper.selectBySvnCommitId(commitId, orderBy, limitOffset),
				svnCommitPathMapper.selectCountBySvnCommitId(commitId));
		
	}
}
