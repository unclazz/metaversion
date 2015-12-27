package org.unclazz.metaversion.mapper;

import java.util.List;

import org.unclazz.metaversion.entity.HasColumnName;
import org.unclazz.metaversion.entity.LimitOffsetClause;
import org.unclazz.metaversion.entity.OrderByClause;
import org.unclazz.metaversion.entity.User;

public interface UserMapper {
	public static enum UserColumn implements HasColumnName {
		ID, NAME, PASSWORD;
		@Override
		public String getColumnName() {
			return name();
		}
	}
	
	int selectNextVal();
	int selectCountAll();
	User selectOneById(int id);
	User selectOneByName(String name);
	List<User> selectListByPartialName(String partialName);
	int insert(User user);
	int update(User user);
	int deleteLogicallyById(int id, LimitOffsetClause limitOffset, OrderByClause orderBy);
}
