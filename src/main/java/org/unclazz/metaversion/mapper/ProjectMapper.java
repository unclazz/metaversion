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

public interface ProjectMapper {
	@Select("SELECT nextval('project_seq') ")
	int selectNextVal();
	
	@Select("SELECT name FROM project WHERE name like ('%' || #{partialName} || '%') "
			+ "ORDER BY name ${limitOffset} ")
	List<String> selectNameByPartialName(@Param("partialName") String partialName, 
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	@Select("SELECT id, code, name, responsible_person responsiblePerson, commit_sign_pattern commitSignPattern "
			+ "FROM project WHERE id = #{id} ")
	Project selectOneById(@Param("id") int id);
	
	@Select("SELECT id, code, name, responsible_person responsiblePerson, commit_sign_pattern commitSignPattern "
			+ "FROM project ${orderBy} ${limitOffset} ")
	List<Project> selectAll(@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	@Select("SELECT count(1) FROM project ")
	int selectCount();
	
	@Select("SELECT id, code, name, responsible_person responsiblePerson, commit_sign_pattern commitSignPattern "
			+ "WHERE name like ('%' || #{partialName} || '%') "
			+ "FROM project ${orderBy} ${limitOffset} ")
	List<Project> selectByPartialName(@Param("partialName") String partialName,
			@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	@Select("SELECT count(1) FROM project "
			+ "WHERE name like ('%' || #{partialName} || '%') ")
	int selectCountByPartialName(@Param("partialName") String partialName);
	
	@Select("SELECT id, code, name, responsible_person responsiblePerson, commit_sign_pattern commitSignPattern "
			+ "WHERE p.id IN ("
			+ "	SELECT pc.project_id "
			+ "	FROM project_svn_commit pc "
			+ "	INNER JOIN svn_commit_path cp "
			+ "	ON pc.svn_commit_id = cp.svn_commit_id "
			+ "	WHERE path like ('%' || #{partialPath} || '%') "
			+ ") ${orderBy} ${limitOffset} ")
	List<Project> selectByPartialPath(@Param("partialPath") String partialPath,
			@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	@Select("SELECT count(1) FROM project p "
			+ "WHERE p.id IN ("
			+ "	SELECT pc.project_id "
			+ "	FROM project_svn_commit pc "
			+ "	INNER JOIN svn_commit_path cp "
			+ "	ON pc.svn_commit_id = cp.svn_commit_id "
			+ "	WHERE path like ('%' || #{partialPath} || '%') "
			+ ") ")
	int selectCountByPartialPath(@Param("partialPath") String partialPath);
	
	// TODO 必要？
	@Select("SELECT count(1) FROM project WHERE id = #{id} ")
	int selectCountById(@Param("id") int id);
	
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
