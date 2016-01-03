package org.unclazz.metaversion.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.ProjectSvnCommit;

public interface ProjectSvnCommitMapper {
	@Insert("INSERT INTO project_svn_commit "
			+ "(project_id, svn_commit_id, create_user_id) "
			+ "VALUES (#{projectId}, #{svnCommitId}, #{auth.id}) ")
	int insert(@Param("projectId") int projectId, @Param("svnCommitId") int svnCommitId, @Param("auth") MVUserDetails auth);
	
	@Insert("INSERT INTO project_svn_commit "
			+ "(project_id, svn_commit_id, create_user_id) "
			+ "VALUES (#{vo.projectId}, #{vo.svnCommitId}, #{auth.id}) ")
	int insert(@Param("vo") ProjectSvnCommit vo, @Param("auth") MVUserDetails auth);
	
	@Delete("DELETE FROM project_svn_commit "
			+ "WHERE project_id = #{vo.projectId} AND svn_commit_id = #{vo.svnCommitId} ")
	int delete(@Param("vo") ProjectSvnCommit vo);
	
	@Delete("DELETE FROM project_svn_commit WHERE project_id = #{projectId} ")
	int deleteByProjectId(@Param("projectId") int projectId);
}
