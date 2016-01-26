package org.unclazz.metaversion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.MVUtils;
import org.unclazz.metaversion.entity.SvnRepository;
import org.unclazz.metaversion.entity.SvnRepositoryStats;
import org.unclazz.metaversion.mapper.ProjectSvnCommitMapper;
import org.unclazz.metaversion.mapper.ProjectSvnRepositoryMapper;
import org.unclazz.metaversion.mapper.SvnCommitMapper;
import org.unclazz.metaversion.mapper.SvnCommitPathMapper;
import org.unclazz.metaversion.mapper.SvnRepositoryMapper;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;
import org.unclazz.metaversion.vo.OrderByClause.Order;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;

@Service
public class RepositoryService {
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private SvnRepositoryMapper svnRepositoryMapper;
	@Autowired
	private ProjectSvnCommitMapper projectSvnCommitMapper;
	@Autowired
	private ProjectSvnRepositoryMapper projectSvnRepositoryMapper;
	@Autowired
	private SvnCommitMapper svnCommitMapper;
	@Autowired
	private SvnCommitPathMapper svnCommitPathMapper;
	@Autowired
	private SvnCommandService svnService;
		
	public void doPasswordEncode(final SvnRepository repository) {
		if (repository.getPassword() == null) {
			return;
		}
		repository.setEncodedPassword(passwordEncoder.encode(MVUtils.
				charArrayToCharSequence(repository.getPassword())));
	}
	
	public void registerRepository(final SvnRepository repository, final MVUserDetails auth) {
		final int firstRevision = svnService.getFirstRevision(repository, 2);
		if (repository.getMaxRevision() < firstRevision) {
			repository.setMaxRevision(firstRevision);
		}
		repository.setId(svnRepositoryMapper.selectNextVal());
		svnRepositoryMapper.insert(repository, auth);
	}
	public void modifyRepository(final SvnRepository repository, final MVUserDetails auth) {
		if (svnRepositoryMapper.update(repository, auth) != 1) {
			throw MVUtils.illegalArgument("Update target repository(id=%s) is not found.", repository.getId());
		}
	}
	
	@Transactional
	public void removeRepository(final int id, final MVUserDetails auth) {
		projectSvnCommitMapper.deleteBySvnRepositoryId(id);
		projectSvnRepositoryMapper.deleteBySvnRepositoryId(id);
		svnCommitPathMapper.deleteBySvnRepositoryId(id);
		svnCommitMapper.deleteBySvnRepositoryId(id);
		
		if (svnRepositoryMapper.delete(id) != 1) {
			throw MVUtils.illegalArgument("Delete target repository(id=%s) is not found.", id);
		}
	}
	public Paginated<SvnRepository> getRepositoryList(final Paging paging) {
		final OrderByClause orderBy = OrderByClause.of("name", Order.ASC);
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(paging);
		return Paginated.of(paging,
				svnRepositoryMapper.selectAll(orderBy, limitOffset),
				svnRepositoryMapper.selectCount());
	}
	public Paginated<SvnRepositoryStats> getRepositoryStatsList(final Paging paging) {
		final OrderByClause orderBy = OrderByClause.of("name", Order.ASC);
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(paging);
		return Paginated.of(paging,
				svnRepositoryMapper.selectStatsAll(orderBy, limitOffset),
				svnRepositoryMapper.selectCountStatsAll());
	}
	public SvnRepositoryStats getRepositoryStats(final int id) {
		return svnRepositoryMapper.selectStatsOneById(id);
	}
	public SvnRepository getRepository(final int id) {
		return svnRepositoryMapper.selectOneById(id);
	}
}
