package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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
	
//	int selectNextVal();
//	int selectCountAll();
	
	@Select("select id, name, password, admin from id = #{id}")
	User selectOneById(@Param("id") int id);
	
	@Select("select id, name, password, admin from name = #{name}")
	User selectOneByName(@Param("name") String name);
	
//	int insert(User user);
//	int update(User user);
//	int deleteLogicallyById(int id, LimitOffsetClause limitOffset, OrderByClause orderBy);
}
