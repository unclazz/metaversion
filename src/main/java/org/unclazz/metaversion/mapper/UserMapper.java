package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.unclazz.metaversion.MVUserDetails;
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
	
	@Select("select nextval('seq_metaversion_user')")
	int selectNextVal();
	
	@Select("select count(1) from metaversion_user where delete_flag = false")
	int selectCountAll();
	
	@Select("select id, name, password, admin from metaversion_user where id = #{id} and delete_flag = false")
	User selectOneById(@Param("id") int id);
	
	@Select("select id, name, password, admin from metaversion_user where name = #{name} and delete_flag = false")
	User selectOneByName(@Param("name") String name);
	
	@Insert("insert into metaversion_user (id, name, password, admin, create_user_id, update_user_id) "
			+ "values (#{user.id}, #{user.name}, #{user.password}, #{user.admin}, #{auth.id}, #{auth.id})")
	int insert(@Param("user") User user, @Param("auth") MVUserDetails auth);
//	int update(User user);
//	int deleteLogicallyById(int id, LimitOffsetClause limitOffset, OrderByClause orderBy);
}
