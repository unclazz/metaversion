package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.ProjectChangedPath;
import org.unclazz.metaversion.entity.SvnCommitPathWithBranchName;
import org.unclazz.metaversion.entity.SvnCommitPathWithRawInfo;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;

public interface SvnCommitPathMapper {
	@Select("SELECT nextval('svn_commit_path_seq') ")
	int selectNextVal();
	
	@Select("SELECT path "
			+ "FROM svn_commit_path cp "
			+ "INNER JOIN project_svn_commit pc "
			+ "ON cp.commit_id = pc.commit_id "
			+ "WHERE path like ('%' || #{partialPath} || '%') "
			+ "GROUP BY path "
			+ "ORDER BY path ${limitOffset} ")
	List<String> selectPathByPartialPath(@Param("partialPath") String partialPath, 
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	@Select("SELECT id, commit_id commitId, change_type_id changeTypeId, "
			+ "path, branch_path_segment branchName "
			+ "FROM svn_commit_path "
			+ "WHERE commit_id = #{commitId} "
			+ "${orderBy} ${limitOffset} ")
	List<SvnCommitPathWithBranchName> selectBySvnCommitId(
			@Param("commitId") int commitId,
			@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	@Select("SELECT count(1) FROM svn_commit_path WHERE commit_id = #{commitId} ")
	int selectCountBySvnCommitId(@Param("commitId") int commitId);
	
	@Select("SELECT path, svn_repository_id repositoryId, svn_repository_name repositoryName, " 
			+ "commit_count commitCount, min_revision minRevision, max_revision maxRevision, " 
			+ "min_commit_date minCommitDate, max_commit_date maxCommitDate " 
			+ "FROM project_changedpath_view " 
			+ "WHERE project_id = #{projectId} " 
			+ "${orderBy} ${limitOffset} ")
	List<ProjectChangedPath> selectByProjectId(
			@Param("projectId") int projectId,
			@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	@Select("SELECT count(1) " 
			+ "FROM  project_changedpath_view " 
			+ "WHERE project_id = #{projectId} ")
	int selectCountByProjectId(@Param("projectId") int projectId);
	
	@Insert("INSERT INTO svn_commit_path "
			+ "(id, commit_id, change_type_id, path, "
			+ "raw_path, base_path_segment, branch_path_segment, create_user_id) "
			+ "VALUES (#{path.id}, #{path.commitId}, #{path.changeTypeId}, #{path.path}, "
			+ "#{path.rawPath}, #{path.basePathSegment}, #{path.branchPathSegment}, #{auth.id}) ")
	int insert(@Param("path") SvnCommitPathWithRawInfo path, @Param("auth") MVUserDetails auth);
	
	@Delete("DELETE FROM svn_commit_path "
			+ "WHERE commit_id IN ("
			+ "	SELECT id "
			+ "	FROM svn_commit "
			+ "	WHERE repository_id = #{repositoryId}) ")
	int deleteBySvnRepositoryId(@Param("repositoryId") int repositoryId);

	@Select("SELECT branch_path_segment "
			+ "FROM svn_commit_path "
			+ "WHERE commit_id = #{commitId} "
			+ "GROUP BY branch_path_segment ")
	List<String> selecBranchNameByCommitId(@Param("commitId") int commitId);
}
