package org.unclazz.metaversion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.VirtualCommitPath;
import org.unclazz.metaversion.mapper.VirtualCommitPathMapper;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;

@Service
public class VirtualCommitPathService {
	@Autowired
	private VirtualCommitPathMapper mapper;
	
	public boolean registerPath(final VirtualCommitPath path, final MVUserDetails auth) {
		path.setId(mapper.selectNextVal());
		return mapper.insert(path, auth) == 1;
	}
	
	public boolean removePath(final int id, final MVUserDetails auth) {
		return mapper.deleteById(id) == 1;
	}
	
	public Paginated<VirtualCommitPath> getPathList(final int projectId,
			final int repositoryId, final Paging page) {
		final OrderByClause orderBy = OrderByClause.of("path");
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(page);
		return Paginated.<VirtualCommitPath>of(page, mapper
				.selectByProjectIdAndRepositoryId(projectId, repositoryId, orderBy, limitOffset),
				mapper.selectCountByProjectIdAndRepositoryId(projectId, repositoryId));
	}
}
