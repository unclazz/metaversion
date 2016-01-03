package org.unclazz.metaversion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unclazz.metaversion.entity.SvnCommit;
import org.unclazz.metaversion.entity.SvnCommitPath;
import org.unclazz.metaversion.mapper.SvnCommitMapper;
import org.unclazz.metaversion.mapper.SvnCommitPathMapper;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;
import org.unclazz.metaversion.vo.OrderByClause.Order;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;

@Service
public class CommitService {
	@Autowired
	private SvnCommitMapper svnCommitMapper;
	@Autowired
	private SvnCommitPathMapper svnCommitPathMapper;
	
	public Paginated<SvnCommit> getProjectUndeterminedCommitList(final int repositoryId, final Paging paging) {
		final OrderByClause orderBy = OrderByClause.of("revision", Order.DESC);
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(paging);
		return Paginated.of(paging,
				svnCommitMapper.selectProjectUndeterminedListByRepositoryId(repositoryId, orderBy, limitOffset),
				svnCommitMapper.selectProjectUndeterminedCountByRepositoryId(repositoryId));
	}
	
	public Paginated<SvnCommit> getCommitListByRepositoryId(final int repositoryId, final Paging paging) {
		final OrderByClause orderBy = OrderByClause.of("revision", Order.DESC);
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(paging);

		// コミット情報を検索する
		return Paginated.of(paging, 
				svnCommitMapper.selectByRepositoryId(repositoryId, orderBy, limitOffset),
				svnCommitMapper.selectCountByRepositoryId(repositoryId));
		
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
