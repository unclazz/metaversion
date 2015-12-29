package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.DmlType;
import org.unclazz.metaversion.entity.LimitOffsetClause;
import org.unclazz.metaversion.entity.OrderByClause;
import org.unclazz.metaversion.entity.User;

public interface UserMapper {
	@Select("select nextval('application_user_seq')")
	int selectNextVal();
	
	@Select("select count(1) from application_user")
	int selectCount();
	
	@Select("select id, name, password, admin from application_user ${orderBy} ${limitOffset} ")
	List<User> selectAll(@Param("orderBy") OrderByClause orderBy, @Param("limitOffset") LimitOffsetClause limitOffset);
	
	@Select("select id, name, password, admin from application_user where id = #{id}")
	User selectOneById(@Param("id") int id);
	
	@Select("select id, name, password, admin from application_user where name = #{name}")
	User selectOneByName(@Param("name") String name);
	
	@Insert("insert into application_user (id, name, password, admin, create_user_id) "
			+ "values (#{user.id}, #{user.name}, #{user.password}, #{user.admin}, #{auth.id})")
	int insert(@Param("user") User user, @Param("auth") MVUserDetails auth);

	@Update("update application_user "
			+ "set name = #{user.name}, password = #{user.password}, admin = #{user.admin} "
			+ "where id = #{user.id} ")
	int update(@Param("user") User user, @Param("auth") MVUserDetails auth);

	@Delete("delete from application_user where id = #{id} ")
	int delete(@Param("id") int id);
	
	@Insert("insert into application_user_history "
			+ "select	*, now(), #{auth.id}, #{dmlType.id} "
			+ "from		application_user where	id = #{user.id} ")
	int insertHistory(@Param("id") int id, @Param("dmlType") DmlType dmlType,  @Param("auth") MVUserDetails auth);
}
