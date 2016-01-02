package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.ProjectSvnRepository;

public interface ProjectSvnRepositoryMapper {
	@Select("SELECT pr.project_id projectId, pr.svn_repository_id svnRepositoryId, pr.last_revision lastRevision "
			+ "FROM project_svn_repository pr "
			+ "INNER JOIN svn_repository r "
			+ "ON pr.repository_id = r.id "
			+ "WHERE pr.last_revision < r.id ")
	List<ProjectSvnRepository> selectObsoletedRecordByProjectId(@Param("projectId") int id);
	
	@Insert("INSERT INTO project_svn_repository "
			+ "(project_id, svn_repository_id, create_user_id, update_user_id) "
			+ "SELECT p.id, r.id, #{auth.id}, #{auth.id} "
			+ "FROM svn_repository r "
			+ "CROSS JOIN project p "
			+ "LEFT OUTER JOIN project_svn_repository pr "
			+ "ON r.id = pr.svn_repository_id AND p.id = pr.project_id "
			+ "WHERE pr.svn_repository_id IS NULL ")
	int insertMissingLink(@Param("auth") MVUserDetails auth);
	
	@Update("UPDATE project_svn_repository "
			+ "SET last_revision = #{record.lastRevision}, update_date = now(), update_user_id = #{auth.id} "
			+ "WHERE project_id = #{record.projectId} AND svn_repository_id = #{record.repositoryId} ")
	int update(@Param("record") ProjectSvnRepository record, @Param("auth") MVUserDetails auth);
	
	@Delete("DELETE FROM project_svn_repository WHERE project_id = #{projectId} ")
	int deleteByProjectId(@Param("projectId") int projectId);
}
