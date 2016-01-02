package org.unclazz.metaversion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.MVUtils;
import org.unclazz.metaversion.entity.SvnRepository;
import org.unclazz.metaversion.mapper.SvnRepositoryMapper;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;
import org.unclazz.metaversion.vo.OrderByClause.Order;
import org.unclazz.metaversion.vo.Paging;

@Service
public class RepositoryService {
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private SvnRepositoryMapper svnRepositoryMapper;
	@Autowired
	private SvnService svnService;
	
	public SvnRepository composeValueObject(final int id, final String name, final String baseUrl,
			final String trunkPathPattern, final String branchPathPattern,
			final int maxRevision, final String username, final char[] password) {
		final SvnRepository r = composeValueObject(name, baseUrl, trunkPathPattern,
				branchPathPattern, maxRevision, username, password);
		r.setId(id);
		return r;
	}
	
	public SvnRepository composeValueObject(final String name, final String baseUrl,
			final String trunkPathPattern, final String branchPathPattern,
			final int maxRevision, final String username, final char[] password) {
		final SvnRepository r = new SvnRepository();
		r.setName(name);
		r.setBaseUrl(baseUrl);
		r.setTrunkPathPattern(trunkPathPattern);
		r.setBranchPathPattern(branchPathPattern);
		r.setMaxRevision(maxRevision);
		r.setUsername(username);
		r.setPassword(passwordEncoder.encode(MVUtils.charArrayToCharSequence(password)));
		return r;
	}
	
	public void registerRepository(final SvnRepository repository, final MVUserDetails auth) {
		final int firstRevision = svnService.getFirstRevision(repository);
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
	public void removeRepository(final int id, final MVUserDetails auth) {
		if (svnRepositoryMapper.delete(id) != 1) {
			throw MVUtils.illegalArgument("Delete target repository(id=%s) is not found.", id);
		}
	}
	public List<SvnRepository> getRepositoryList(final Paging paging) {
		final OrderByClause orderBy = OrderByClause.of("name", Order.ASC);
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(paging);
		return svnRepositoryMapper.selectAll(orderBy, limitOffset);
	}
	public int getRepositoryCount() {
		return svnRepositoryMapper.selectCount();
	}
	public SvnRepository getRepository(final int id) {
		return svnRepositoryMapper.selectOneById(id);
	}
}
