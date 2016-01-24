package org.unclazz.metaversion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.MVUtils;
import org.unclazz.metaversion.entity.Project;
import org.unclazz.metaversion.entity.ProjectStats;
import org.unclazz.metaversion.mapper.ProjectMapper;
import org.unclazz.metaversion.mapper.ProjectSvnCommitMapper;
import org.unclazz.metaversion.mapper.ProjectSvnRepositoryMapper;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;
import org.unclazz.metaversion.vo.ProjectSearchCondition;
import org.unclazz.metaversion.vo.OrderByClause.Order;

@Service
public class ProjectService {
	@Autowired
	private ProjectMapper projectMapper;
	@Autowired
	private ProjectSvnCommitMapper projectSvnCommitMapper;
	@Autowired
	private ProjectSvnRepositoryMapper projectSvnRepositoryMapper;
	
	public List<String> getProjectNameList(final String partialName, final int size) {
		final LimitOffsetClause limitOffset = LimitOffsetClause.ofLimit(size);
		return projectMapper.selectNameByPartialName(partialName, limitOffset);
	}
	
	public Project getProjectById(final int id) {
		return projectMapper.selectOneById(id);
	}
	
	public ProjectStats getProjectStatsById(final int id) {
		return projectMapper.selectStatsOneById(id);
	}
	
	public Paginated<Project> getProjectListAll(final Paging paging) {
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(paging);
		final OrderByClause orderBy = OrderByClause.of("name", Order.ASC);
		return Paginated.of(paging,
				projectMapper.selectAll(orderBy, limitOffset),
				projectMapper.selectCount());
	}
	
	public Paginated<Project> getProjectListByCondition(final ProjectSearchCondition cond) {
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(cond.getPaging());
		final OrderByClause orderBy = OrderByClause.of("name", Order.ASC);
		
		if (cond.isPathbase()) {
			return Paginated.of(cond.getPaging(),
					projectMapper.selectByPartialPath(cond.getLike(), 
							cond.getUnlinkedCommitId(), orderBy, limitOffset),
					projectMapper.selectCountByPartialPath(cond.getLike(), 
							cond.getUnlinkedCommitId()));
		} else {
			return Paginated.of(cond.getPaging(),
						projectMapper.selectByPartialName(
								cond.getLike(), 
								cond.getUnlinkedCommitId(), 
								orderBy, 
								limitOffset),
						projectMapper.selectCountByPartialName(
								cond.getLike(), 
								cond.getUnlinkedCommitId()));
		}
	}
	
	public Paginated<Project> getProjectListByCommitId(final int commitId, final Paging paging) {
		final LimitOffsetClause limitOffset = LimitOffsetClause.of(paging);
		final OrderByClause orderBy = OrderByClause.of("name", Order.ASC);
		return Paginated.of(paging,
				projectMapper.selectByCommitId(commitId, orderBy, limitOffset),
				projectMapper.selectCountByCommitId(commitId));
	}
	
	public void regisiterProject(final Project project, final MVUserDetails auth) {
		project.setId(projectMapper.selectNextVal());
		if (projectMapper.insert(project, auth) != 1) {
			throw MVUtils.illegalArgument("Unexpected error has occurred. Insert operation failed.", project.getId());
		}
	}
	
	public void modifyProject(final Project project, final MVUserDetails auth) {
		if (projectMapper.update(project, auth) != 1) {
			throw MVUtils.illegalArgument("Unknown project(id=%s). Update operation failed.", project.getId());
		}
	}
	
	@Transactional
	public void removeProjectById(final int projectId) {
		projectSvnCommitMapper.deleteByProjectId(projectId);
		projectSvnRepositoryMapper.deleteByProjectId(projectId);
		if (projectMapper.delete(projectId) != 1) {
			throw MVUtils.illegalArgument("Unknown project(id=%s). Update operation failed.", projectId);
		}
	}
}
