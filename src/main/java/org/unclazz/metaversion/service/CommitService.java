package org.unclazz.metaversion.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unclazz.metaversion.MVProperties;
import org.unclazz.metaversion.entity.ProjectChangedPath;
import org.unclazz.metaversion.entity.SvnCommit;
import org.unclazz.metaversion.entity.SvnCommitPathWithBranchName;
import org.unclazz.metaversion.entity.SvnCommitStats;
import org.unclazz.metaversion.entity.SvnCommitWithRepositoryInfo;
import org.unclazz.metaversion.mapper.SvnCommitMapper;
import org.unclazz.metaversion.mapper.SvnCommitPathMapper;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;
import org.unclazz.metaversion.vo.OrderByClause.Order;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;
import org.unclazz.metaversion.vo.ProjectCommitSearchCondition;

@Service
public class CommitService {
	@Autowired
	private MVProperties props;
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

		final List<SvnCommitStats> cs = svnCommitMapper.selectStatsByRepositoryId(repositoryId, orderBy, limitOffset);
		for (final SvnCommitStats c : cs) {
			final List<String> bns = getBranchNameListByCommitId(c.getId());
			c.setBranchNames(bns);
			c.setBranchCount(bns.size());
		}
		
		// コミット情報を検索する
		return Paginated.of(paging, cs,
				svnCommitMapper.selectStatsCountByRepositoryId(repositoryId));
		
	}
	
	public SvnCommitStats getCommitStatsByCommitId(final int commitId) {
		final SvnCommitStats c = svnCommitMapper.selectStatsOneByCommitId(commitId);
		final List<String> bns = getBranchNameListByCommitId(commitId);
		c.setBranchNames(bns);
		c.setBranchCount(bns.size());
		return c;
	}
	
	private List<String> getBranchNameListByCommitId(final int commitId) {
		final LinkedList<String> ns = new LinkedList<String>();
		for (final String bn : svnCommitPathMapper.selecBranchNameByCommitId(commitId)) {
			if (bn.startsWith("/")) {
				ns.add(bn.substring(1));
			} else {
				ns.add(bn);
			}
		}
		return ns;
	}
	
	public Paginated<SvnCommitWithRepositoryInfo> getCommitListByProjectId(final int projectId, final Paging paging) {
		final OrderByClause orderBy = OrderByClause.of("revision", Order.DESC);
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(paging);

		final List<SvnCommitWithRepositoryInfo> cs = svnCommitMapper
				.selectByProjectId(projectId, orderBy, limitOffset);
		populateBranchNameList(cs);
		
		// コミット情報を検索する
		return Paginated.of(paging, cs,
				svnCommitMapper.selectCountByProjectId(projectId));
	}
	
	public Paginated<SvnCommitWithRepositoryInfo> getCommitListByCondition(final ProjectCommitSearchCondition cond) {
		final OrderByClause orderBy = OrderByClause.of("revision", Order.DESC);
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(cond.getPaging());

		if (cond.isPathbase()) {
			final List<SvnCommitWithRepositoryInfo> cs = svnCommitMapper
					.selectByProjectIdAndPartialPath(cond, orderBy, limitOffset);
			populateBranchNameList(cs);
			
			// コミット情報を検索する
			return Paginated.of(cond.getPaging(), cs,
					svnCommitMapper.selectCountByProjectIdAndPartialPath(cond));
		} else {
			final List<SvnCommitWithRepositoryInfo> cs = svnCommitMapper
					.selectByProjectIdAndPartialMessage(cond, orderBy, limitOffset);
			populateBranchNameList(cs);
			
			// コミット情報を検索する
			return Paginated.of(cond.getPaging(), cs,
					svnCommitMapper.selectCountByProjectIdAndPartialMessage(cond));
		}
	}
	
	private void populateBranchNameList(List<SvnCommitWithRepositoryInfo> cs) {
		for (final SvnCommitWithRepositoryInfo c : cs) {
			final List<String> bns = getBranchNameListByCommitId(c.getId());
			c.setBranchCount(bns.size());
			c.setBranchNames(bns);
		}
	}
	
	public SvnCommitWithRepositoryInfo getCommitWithRepositoryInfoByCommitId(final int commitId) {
		final SvnCommitWithRepositoryInfo c = svnCommitMapper
				.selectWithRepositoryInfoById(commitId);
		final List<String> bns = getBranchNameListByCommitId(commitId);
		c.setBranchCount(bns.size());
		c.setBranchNames(bns);
		return c;
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

	public byte[] getProjectChangedPathCsvByProjectId(final int id) throws IOException {
		final OrderByClause orderBy = OrderByClause.of("repositoryName").and("path");
		final LimitOffsetClause limitOffset = LimitOffsetClause.ALL;
		
		// CSVファイルのコンテンツを一時的に格納するためバイト配列出力ストリームを初期化
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		// そのストリームをラップするかたちでライターを初期化
		final OutputStreamWriter osw = new OutputStreamWriter(os, props.getCsvCharset());
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
		return os.toByteArray();
	}
	public Paginated<SvnCommitPathWithBranchName> getChangedPathListByRepositoryIdAndCommitId(
			final int repositoryId, final int commitId, final Paging paging) {
		// ＊検索にはリポジトリIDは利用しないが概念的に親子関係（親＝リポジトリ、子＝コミット）にあるため
		// 念のため引数に取るようにしている。今後エンティティ構造に変更があった場合に役立つかもしれない。
		
		final OrderByClause orderBy = OrderByClause.of("path");
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(paging);
		
		final List<SvnCommitPathWithBranchName> ps = svnCommitPathMapper.
				selectBySvnCommitId(commitId, orderBy, limitOffset);
		for (final SvnCommitPathWithBranchName p : ps) {
			final String bn = p.getBranchName();
			if (bn != null && bn.startsWith("/")) {
				p.setBranchName(bn.substring(1));
			}
		}
		
		// パス情報を検索する
		return Paginated.of(paging, ps,
				svnCommitPathMapper.selectCountBySvnCommitId(commitId));
	}
	
	/**
	 * リポジトリの変更パスの一覧を取得する.
	 * @param repositoryId リポジトリID. このリポジトリのパスがコミット横断的に検索される。
	 * @param partialPath 部分パス文字列. この文字列を含まないパスは結果から除外される。
	 * 						{@code null}もしくは空文字列の場合、除外は行われない。
	 * @param unlinkedTo プロジェクトID. このプロジェクトと紐付けられたパスは結果から除外される。
	 * 						{@code 0}の場合、除外は行われない。
	 * @param paging ページング情報.
	 * @return ページ付けされた変更パス一覧
	 */
	public Paginated<String> getChangedPathListByRepositoryIdAndPartialPath(
			final int repositoryId, final String partialPath, final int unlinkedTo, 
			final Paging paging) {
		final OrderByClause orderBy = OrderByClause.of("path");
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(paging);
		return Paginated.<String>of(paging, 
				svnCommitPathMapper.selectPathNameByRepositoryIdAndPartialPath
				(repositoryId, partialPath, unlinkedTo, orderBy, limitOffset),
				svnCommitPathMapper.selectCountPathNameByRepositoryIdAndPartialPath
				(repositoryId, partialPath, unlinkedTo));
	}
}
