package org.unclazz.metaversion.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.ProjectSvnCommit;

public interface ProjectSvnCommitMapper {
	@Insert("SELECT count(1) FROM project_svn_commit "
			+ "WHERE project_id = #{projectId} AND commit_id = #{commitId} ")
	int selectCountByProjectIdAndCommitId(@Param("projectId") int projectId, @Param("commitId") int commitId);
	
	@Insert("INSERT INTO project_svn_commit "
			+ "(project_id, commit_id, create_user_id) "
			+ "VALUES (#{vo.projectId}, #{vo.commitId}, #{auth.id}) ")
	int insert(@Param("vo") ProjectSvnCommit vo, @Param("auth") MVUserDetails auth);
	
	@Delete("DELETE FROM project_svn_commit "
			+ "WHERE project_id = #{vo.projectId} AND commit_id = #{vo.commitId} ")
	int delete(@Param("vo") ProjectSvnCommit vo);
	
	@Delete("DELETE FROM project_svn_commit WHERE project_id = #{projectId} ")
	int deleteByProjectId(@Param("projectId") int projectId);
	
	@Delete("DELETE FROM project_svn_commit "
			+ "WHERE commit_id IN ( "
			+ "	SELECT id "
			+ "	FROM svn_commit "
			+ "	WHERE repository_id = #{repositoryId}) ")
	int deleteBySvnRepositoryId(@Param("repositoryId") int repositoryId);
}
