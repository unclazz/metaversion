package org.unclazz.metaversion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.ProjectVirtualChangedPath;
import org.unclazz.metaversion.entity.VirtualChangedPath;
import org.unclazz.metaversion.mapper.VirtualChangedPathMapper;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;

@Service
public class VirtualChangedPathService {
	@Autowired
	private VirtualChangedPathMapper mapper;
	
	public boolean registerPath(final VirtualChangedPath path, final MVUserDetails auth) {
		path.setId(mapper.selectNextVal());
		return mapper.insert(path, auth) == 1;
	}
	
	public boolean removePath(final int id, final MVUserDetails auth) {
		return mapper.deleteById(id) == 1;
	}
	
	public Paginated<ProjectVirtualChangedPath> getPathListByProjectId(final int projectId, final Paging page) {
		final OrderByClause orderBy = OrderByClause.of("path");
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(page);
		return Paginated.<ProjectVirtualChangedPath>of(page, mapper
				.selectByProjectId(projectId, orderBy, limitOffset),
				mapper.selectCountByProjectId(projectId));
	}
}
