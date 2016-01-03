package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.SvnCommit;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;

public interface SvnCommitMapper {
	@Select("SELECT nextval('svn_commit_seq') ")
	int selectNextVal();
	
	@Select("SELECT id, svn_repository_id svnRepositoryId, commit_message commitMessage, "
			+ "commit_date commitDate, committer_name committerName, revision "
			+ "FROM svn_commit WHERE id = #{id} ")
	SvnCommit selectOneById(@Param("id") int id);
	
	@Select("SELECT c.id, c.svn_repository_id svnRepositoryId, c.commit_message commitMessage, "
			+ "c.commit_date commitDate, c.committer_name committerName, c.revision "
			+ "FROM svn_commit c "
			+ "INNER JOIN project_svn_repository pr "
			+ "ON c.svn_repository_id = pr.svn_repository_id "
			+ "WHERE c.revision > pr.last_revision "
			+ "AND pr.project_id = #{projectId} AND pr.svn_repository_id = #{svnRepositoryId} ")
	List<SvnCommit> selectForMatchingByProjectIdAndRepositoryId(
			@Param("projectId") int projectId, @Param("svnRepositoryId") int svnRepositoryId);
	
	@Select(" SELECT c.id, c.svn_repository_id svnRepositoryId, c.commit_message commitMessage, "
			+ "c.commit_date commitDate, c.committer_name committerName, c.revision "
			+ "FROM svn_commit c "
			+ "LEFT OUTER JOIN project_svn_commit pc "
			+ "ON c.id = pc.svn_commit_id "
			+ "WHERE pc.svn_commit_id IS NULL AND c.svn_repository_id = #{svnRepositoryId} "
			+ "${orderBy} ${limitOffset} ")
	List<SvnCommit> selectProjectUndeterminedListByRepositoryId(
			@Param("svnRepositoryId") int svnRepositoryId,
			@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	@Select(" SELECT count(1) FROM svn_commit c "
			+ "LEFT OUTER JOIN project_svn_commit pc "
			+ "ON c.id = pc.svn_commit_id "
			+ "WHERE pc.svn_commit_id IS NULL AND c.svn_repository_id = #{svnRepositoryId} ")
	int selectProjectUndeterminedCountByRepositoryId(@Param("svnRepositoryId") int svnRepositoryId);
	
	@Insert("INSERT INTO svn_commit (id, svn_repository_id, commit_message, "
			+ "commit_date, committer_name, revision, create_user_id) "
			+ "VALUES (#{commit.id}, #{commit.svnRepositoryId}, #{commit.commitMessage}, "
			+ "#{commit.commitDate}, #{commit.committerName}, #{commit.revision}, #{auth.id}) ")
	int insert(@Param("commit") SvnCommit commit, @Param("auth") MVUserDetails auth);
}
