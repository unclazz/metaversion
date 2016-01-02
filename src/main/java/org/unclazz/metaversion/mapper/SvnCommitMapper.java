package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.SvnCommit;

public interface SvnCommitMapper {
	@Select("SELECT nextval('svn_commit_seq') ")
	int selectNextVal();
	
	@Select("SELECT id, svn_repository_id svnRepositoryId, commit_message commitMessage, "
			+ "commit_date commitDate, committer_name committerName, revision "
			+ "FROM svn_commit WHERE id = #{id} ")
	SvnCommit selectOneById(@Param("id") int id);
	
	@Select("SELECT c.id, c.svn_repository_id svnRepositoryId, commit_message commitMessage, "
			+ "commit_date commitDate, committer_name committerName, revision "
			+ "FROM svn_commit c "
			+ "INNER JOIN project_svn_repository pr "
			+ "ON c.svn_repository_id = pr.svn_repository_id "
			+ "WHERE c.revision > pr.last_revision "
			+ "AND pr.project_id = #{projectId} AND pr.svn_repository_id = #{svnRepositoryId} ")
	List<SvnCommit> selectForMatchingByProjectIdAndRepositoryId(
			@Param("projectId") int projectId, @Param("svnRepositoryId") int svnRepositoryId);
	
	@Insert("INSERT INTO svn_commit (id, svn_repository_id, commit_message, "
			+ "commit_date, committer_name, revision, create_user_id) "
			+ "VALUES (#{commit.id}, #{commit.svnRepositoryId}, #{commit.commitMessage}, "
			+ "#{commit.commitDate}, #{commit.committerName}, #{commit.revision}, #{auth.id}) ")
	int insert(@Param("commit") SvnCommit commit, @Param("auth") MVUserDetails auth);
}
