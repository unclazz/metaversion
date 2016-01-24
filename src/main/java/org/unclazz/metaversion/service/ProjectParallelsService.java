package org.unclazz.metaversion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unclazz.metaversion.entity.ProjectParallels;
import org.unclazz.metaversion.mapper.ProjectParallelsMapper;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;

@Service
public class ProjectParallelsService {
	@Autowired
	private ProjectParallelsMapper projectParallelsMapper;
	
	public Paginated<ProjectParallels> getProjectParallelsByProjectId(final int projectId, final Paging paging) {
		final OrderByClause orderBy = OrderByClause.of("path");
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(paging);

		// コミット情報を検索する
		return Paginated.of(paging, 
				projectParallelsMapper.selectByProjectId(projectId, orderBy, limitOffset),
				projectParallelsMapper.selectCountByProjectId(projectId));
		
	}
}
