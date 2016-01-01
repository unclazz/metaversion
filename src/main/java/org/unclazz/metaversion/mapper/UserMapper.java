package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.User;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;

public interface UserMapper {
	@Select("SELECT nextval('application_user_seq')")
	int selectNextVal();
	
	@Select("SELECT count(1) FROM application_user")
	int selectCount();
	
	@Select("SELECT id, name, admin FROM application_user ${orderBy} ${limitOffset} ")
	List<User> selectUserHasNoPasswordAll(@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	@Select("SELECT id, name, admin FROM application_user WHERE id = #{id}")
	User selectUserHasNoPasswordOneById(@Param("id") int id);
	
	@Select("SELECT id, name, password, admin FROM application_user WHERE id = #{id}")
	User selectOneById(@Param("id") int id);
	
	@Select("SELECT id, name, password, admin FROM application_user WHERE name = #{name}")
	User selectOneByName(@Param("name") String name);
	
	@Insert("INSERT INTO application_user (id, name, password, admin, create_user_id, update_user_id) "
			+ "VALUES (#{user.id}, #{user.name}, #{user.password}, #{user.admin}, #{auth.id}, #{auth.id})")
	int insert(@Param("user") User user, @Param("auth") MVUserDetails auth);

	@Update("UPDATE application_user "
			+ "SET name = #{user.name}, password = #{user.password}, admin = #{user.admin}, "
			+ "update_date = now(), update_user_id = #{auth.id} "
			+ "WHERE id = #{user.id} ")
	int update(@Param("user") User user, @Param("auth") MVUserDetails auth);

	@Delete("DELETE FROM application_user WHERE id = #{id} and id <> 0 ")
	int delete(@Param("id") int id);
}
