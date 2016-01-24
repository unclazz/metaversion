package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.SvnCommit;
import org.unclazz.metaversion.entity.SvnCommitStats;
import org.unclazz.metaversion.entity.SvnCommitWithRepositoryInfo;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;

public interface SvnCommitMapper {
	@Select("SELECT nextval('svn_commit_seq') ")
	int selectNextVal();
	
	@Select("SELECT id, repository_id repositoryId, commit_message commitMessage, "
			+ "commit_date commitDate, committer_name committerName, revision "
			+ "FROM svn_commit WHERE id = #{id} ")
	SvnCommit selectOneById(@Param("id") int id);
	
	@Select("SELECT c.id, repository_id repositoryId, r.name repositoryName, r.base_url repositoryBaseUrl, "
			+ "commit_message commitMessage, commit_date commitDate, committer_name committerName, revision "
			+ "FROM svn_commit c "
			+ "INNER JOIN svn_repository r "
			+ "ON c.repository_id = r.id "
			+ "WHERE c.id = #{id} ")
	SvnCommitWithRepositoryInfo selectWithRepositoryInfoById(@Param("id") int id);
	
	@Select("SELECT c.id, c.repository_id repositoryId, c.commit_message commitMessage, "
			+ "c.commit_date commitDate, c.committer_name committerName, c.revision "
			+ "FROM svn_commit c "
			+ "INNER JOIN project_svn_repository pr "
			+ "ON c.repository_id = pr.repository_id "
			+ "WHERE c.revision > pr.last_revision "
			+ "AND pr.project_id = #{projectId} AND pr.repository_id = #{repositoryId} ")
	List<SvnCommit> selectForMatchingByProjectIdAndRepositoryId(
			@Param("projectId") int projectId, @Param("repositoryId") int repositoryId);
	
	@Select(" SELECT c.id, c.repository_id repositoryId, c.commit_message commitMessage, "
			+ "c.commit_date commitDate, c.committer_name committerName, c.revision "
			+ "FROM svn_commit c "
			+ "LEFT OUTER JOIN project_svn_commit pc "
			+ "ON c.id = pc.commit_id "
			+ "WHERE pc.commit_id IS NULL AND c.repository_id = #{repositoryId} "
			+ "${orderBy} ${limitOffset} ")
	List<SvnCommit> selectProjectUndeterminedListByRepositoryId(
			@Param("repositoryId") int repositoryId,
			@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	@Select(" SELECT count(1) FROM svn_commit c "
			+ "LEFT OUTER JOIN project_svn_commit pc "
			+ "ON c.id = pc.commit_id "
			+ "WHERE pc.commit_id IS NULL AND c.repository_id = #{repositoryId} ")
	int selectProjectUndeterminedCountByRepositoryId(@Param("repositoryId") int repositoryId);
	
	@Select(" SELECT c.id, c.repository_id repositoryId, c.commit_message commitMessage, "
			+ "c.commit_date commitDate, c.committer_name committerName, c.revision "
			+ "FROM svn_commit c "
			+ "WHERE c.repository_id = #{repositoryId} "
			+ "${orderBy} ${limitOffset} ")
	List<SvnCommit> selectByRepositoryId(
			@Param("repositoryId") int repositoryId,
			@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	@Select(" SELECT count(1) FROM svn_commit c "
			+ "WHERE c.repository_id = #{repositoryId} ")
	int selectCountByRepositoryId(@Param("repositoryId") int repositoryId);
	
	@Select(" SELECT c.id, c.repository_id repositoryId, c.commit_message commitMessage, "
			+ "c.commit_date commitDate, c.committer_name committerName, c.revision,"
			+ "c.project_count projectCount, c.path_count pathCount, "
			+ "c.min_project_id projectId, c.min_project_code projectCode, c.min_project_name projectName "
			+ "FROM svn_commit_stats_view c "
			+ "WHERE c.id = #{commitId} ")
	SvnCommitStats selectStatsOneByCommitId(
			@Param("commitId") int commitId);
	
	@Select(" SELECT c.id, c.repository_id repositoryId, c.commit_message commitMessage, "
			+ "c.commit_date commitDate, c.committer_name committerName, c.revision,"
			+ "c.project_count projectCount, c.path_count pathCount, "
			+ "c.min_project_id projectId, c.min_project_code projectCode, c.min_project_name projectName "
			+ "FROM svn_commit_stats_view c "
			+ "WHERE c.repository_id = #{repositoryId} "
			+ "${orderBy} ${limitOffset} ")
	List<SvnCommitStats> selectStatsByRepositoryId(
			@Param("repositoryId") int repositoryId,
			@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	@Select(" SELECT count(1) FROM svn_commit_stats_view c "
			+ "WHERE c.repository_id = #{repositoryId} ")
	int selectStatsCountByRepositoryId(@Param("repositoryId") int repositoryId);
	
	@Select(" SELECT c.id, c.repository_id repositoryId, c.commit_message commitMessage, "
			+ "c.commit_date commitDate, c.committer_name committerName, c.revision, "
			+ "r.name repositoryName, r.base_url repositoryBaseUrl "
			+ "FROM svn_commit c "
			+ "INNER JOIN project_svn_commit pc "
			+ "ON c.id = pc.commit_id "
			+ "INNER JOIN svn_repository r "
			+ "ON r.id = c.repository_id "
			+ "WHERE pc.project_id = #{projectId} "
			+ "${orderBy} ${limitOffset} ")
	List<SvnCommitWithRepositoryInfo> selectByProjectId(
			@Param("projectId") int projectId,
			@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	@Select(" SELECT count(1) FROM svn_commit c "
			+ "INNER JOIN project_svn_commit pc "
			+ "ON c.id = pc.commit_id "
			+ "WHERE pc.project_id = #{projectId} ")
	int selectCountByProjectId(@Param("projectId") int projectId);
	
	@Insert("INSERT INTO svn_commit (id, repository_id, commit_message, "
			+ "commit_date, committer_name, revision, create_user_id) "
			+ "VALUES (#{commit.id}, #{commit.repositoryId}, #{commit.commitMessage}, "
			+ "#{commit.commitDate}, #{commit.committerName}, #{commit.revision}, #{auth.id}) ")
	int insert(@Param("commit") SvnCommit commit, @Param("auth") MVUserDetails auth);
	
	@Delete("DELETE FROM svn_commit WHERE repository_id = #{repositoryId} ")
	int deleteBySvnRepositoryId(@Param("repositoryId") int repositoryId);
}
