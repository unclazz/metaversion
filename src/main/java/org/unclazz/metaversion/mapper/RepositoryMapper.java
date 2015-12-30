package org.unclazz.metaversion.mapper;

import java.util.List;

import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.DmlType;
import org.unclazz.metaversion.entity.LimitOffsetClause;
import org.unclazz.metaversion.entity.OrderByClause;
import org.unclazz.metaversion.entity.Repository;

public interface RepositoryMapper {
	int selectNextVal();
	int selectCount();
	Repository selectOneById(int id);
	List<Repository> selectAll(OrderByClause orderBy, LimitOffsetClause limitOffset);
	int insert(Repository repository, MVUserDetails auth);
	int update(Repository repository, MVUserDetails auth);
	int delete(int id);
	int insertHistory(int id, DmlType dmlType, MVUserDetails auth);
}
