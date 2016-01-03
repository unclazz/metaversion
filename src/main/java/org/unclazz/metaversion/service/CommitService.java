package org.unclazz.metaversion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unclazz.metaversion.entity.SvnCommit;
import org.unclazz.metaversion.mapper.SvnCommitMapper;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;
import org.unclazz.metaversion.vo.OrderByClause.Order;
import org.unclazz.metaversion.vo.Paging;

@Service
public class CommitService {
	@Autowired
	private SvnCommitMapper svnCommitMapper;
	
	public List<SvnCommit> getProjectUndeterminedCommitList(final int repositoryId, final Paging paging) {
		final OrderByClause orderBy = OrderByClause.of("revision", Order.DESC);
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(paging);
		return svnCommitMapper.selectProjectUndeterminedListByRepositoryId(repositoryId, orderBy, limitOffset);
	}
	public int getProjectUndeterminedCommitCount(final int repositoryId) {
		return svnCommitMapper.selectProjectUndeterminedCountByRepositoryId(repositoryId);
	}
}
