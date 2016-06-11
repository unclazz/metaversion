package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.ProjectVirtualChangedPath;
import org.unclazz.metaversion.entity.VirtualChangedPath;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;

public interface VirtualChangedPathMapper {
	@Select("SELECT nextval('virtual_changed_path_seq') ")
	int selectNextVal();
	
	@Select("SELECT vcp.id virtualChangedPathId, vcp.project_id projectId, "
			+ "		vcp.repository_id repositoryId, r.name repositoryName, "
			+ "		vcp.change_type_id changeTypeId, vcp.path "
			+ "FROM virtual_changed_path vcp "
			+ "INNER JOIN svn_repository r "
			+ "ON repository_id =  r.id "
			+ "WHERE vcp.id = #{id} ")
	ProjectVirtualChangedPath selectOneById(@Param("id") int id);
	
	@Select("SELECT vcp.id virtualChangedPathId, vcp.project_id projectId, "
			+ "		vcp.repository_id repositoryId, r.name repositoryName, "
			+ "		vcp.change_type_id changeTypeId, vcp.path "
			+ "FROM virtual_changed_path vcp "
			+ "INNER JOIN svn_repository r "
			+ "ON repository_id =  r.id "
			+ "WHERE project_id = #{projectId} "
			+ "${orderBy} ${limitOffset} ")
	List<ProjectVirtualChangedPath> selectByProjectId(
			@Param("projectId") int projectId,
			@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	@Select("SELECT count(1) "
			+ "FROM virtual_changed_path "
			+ "WHERE project_id = #{projectId} ")
	int selectCountByProjectId(@Param("projectId") int projectId);
	
	@Insert("INSERT INTO virtual_changed_path "
			+ "(id, project_id, repository_id, change_type_id, path) "
			+ "VALUES (#{vcp.id}, #{vcp.projectId}, "
			+ "#{vcp.repositoryId}, #{vcp.changeTypeId}, #{vcp.path}) ")
	int insert(@Param("vcp") VirtualChangedPath path, @Param("auth") MVUserDetails auth);
	
	@Delete("DELETE FROM virtual_changed_path WHERE id = #{id} ")
	int deleteById(@Param("id") int id);
	
	@Delete("DELETE FROM virtual_changed_path WHERE project_id = #{projectId} ")
	int deleteByProjectId(@Param("projectId") int projectId);
}
