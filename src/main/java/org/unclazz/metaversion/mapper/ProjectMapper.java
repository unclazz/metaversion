package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.Project;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;

//TODO MyBatisアノテーションの設定
public interface ProjectMapper {
	@Select("SELECT nextval('project_seq') ")
	int selectNextVal();
	
	@Select("SELECT id, code, name, responsible_person responsiblePerson, commit_sign_pattern commitSignPattern "
			+ "FROM project WHERE id = #{id} ")
	Project selectOneById(@Param("id") int id);
	
	@Select("SELECT count(1) FROM project ")
	Project selectCount(@Param("id") int id);
	
	@Select("SELECT count(1) FROM project ${orderBy} ${limitOffset} ")
	List<Project> selectAll(@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	@Insert("INSERT INTO project "
			+ "(id, code, name, responsible_person, commit_sign_pattern, create_user_id, update_user_id) "
			+ "VALUES (#{proj.id}, #{proj.code}, #{proj.name}, #{proj.responsiblePerson}, #{proj.commitSignPattern}, "
			+ "#{auth.id}, #{auth.id}) ")
	int insert(@Param("proj") Project project, @Param("auth") MVUserDetails auth);

	@Update("UPDATE project "
			+ "SET code = #{proj.code}, name = #{proj.name}, responsible_person = #{proj.responsiblePerson}, "
			+ "commit_sign_pattern = #{proj.commitSignPattern}, update_user_id = #{auth.id} "
			+ "WHERE id = #{proj.id} ")
	int update(@Param("proj") Project project, @Param("auth") MVUserDetails auth);

	@Update("DELETE FROM project WHERE id = #{proj.id} ")
	int delete(@Param("id") int id);
}
