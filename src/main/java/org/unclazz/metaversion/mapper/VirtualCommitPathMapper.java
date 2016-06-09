package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.VirtualCommitPath;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;

public interface VirtualCommitPathMapper {
	@Select("SELECT nextval('virtual_commit_path_seq') ")
	int selectNextVal();
	
	@Select("SELECT id, project_id projectId, repositoryId, changeTypeId, path "
			+ "FROM virtual_commit_path "
			+ "WHERE project_id = #{projectId} "
			+ "AND repository_id = #{repositoryId} "
			+ "${orderBy} ${limitOffset} ")
	List<VirtualCommitPath> selectByProjectIdAndRepositoryId(
			@Param("projectId") int projectId,
			@Param("repositoryId") int repositoryId,
			@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	@Select("SELECT count(1) "
			+ "FROM virtual_commit_path "
			+ "WHERE project_id = #{projectId} "
			+ "AND repository_id = #{repositoryId} ")
	int selectCountByProjectIdAndRepositoryId(
			@Param("projectId") int projectId,
			@Param("repositoryId") int repositoryId);
	
	@Insert("INSERT INTO virtual_commit_path "
			+ "(id, project_id, repository_id, change_type_id, path) "
			+ "VALUES (#{vcp.id}, #{vcp.projectId}, "
			+ "#{vcp.repositoryId}, #{vcp.changeTypeId}, #{vcp.path}) ")
	int insert(@Param("vcp") VirtualCommitPath path, @Param("auth") MVUserDetails auth);
	
	@Delete("DELETE FROM virtual_commit_path WHERE id = #{id} ")
	int deleteById(@Param("id") int id);
	
	@Delete("DELETE FROM virtual_commit_path WHERE project_id = #{projectId} ")
	int deleteByProjectId(@Param("projectId") int projectId);
}
