package org.unclazz.metaversion.mapper;

import java.util.List;

import org.unclazz.metaversion.entity.LimitOffsetClause;
import org.unclazz.metaversion.entity.OrderByClause;
import org.unclazz.metaversion.entity.Project;

public interface ProjectMapper {
	int selectNextVal();
	int selectCountAll();
	int selectCountByPartialName(String partialName);
	Project selectOneById(int id);
	List<Project> selectListByPartialName(String partialName, LimitOffsetClause lo, OrderByClause ob);
	List<Project> selectListByPartialNameExceptClosed(String partialName, LimitOffsetClause lo, OrderByClause ob);
	int insert(Project project);
	int update(Project project);
	int deleteLogicallyById(int id);
}
