package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.PathAndItsSvnRepository;
import org.unclazz.metaversion.entity.SvnCommitPath;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;

public interface SvnCommitPathMapper {
	@Select("SELECT nextval('svn_commit_path_seq') ")
	int selectNextVal();
	
	@Select("SELECT path "
			+ "FROM svn_commit_path cp "
			+ "INNER JOIN project_svn_commit pc "
			+ "ON cp.svn_commit_id = pc.svn_commit_id "
			+ "WHERE path like ('%' || #{partialName} || '%') "
			+ "GROUP BY path "
			+ "ORDER BY path ${limitOffset} ")
	List<String> selectPathByPartialPath(@Param("partialPath") String partialPath, 
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	// TODO つかう？
	@Select("SELECT id, svn_commit_id svnCommitId, change_type_id changeTypeId, path "
			+ "FROM svn_commit_path WHERE svn_commit_id = #{svnCommitId} ")
	List<SvnCommitPath> selectBySvnCommitId(@Param("svnCommitId") int svnCommitId);
	
	@Select("SELECT id, svn_commit_id svnCommitId, change_type_id changeTypeId, path "
			+ "FROM svn_commit_path "
			+ "WHERE svn_commit_id = #{svnCommitId} "
			+ "${orderBy} ${limitOffset} ")
	List<SvnCommitPath> selectBySvnCommitId(
			@Param("svnCommitId") int svnCommitId,
			@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	@Select("SELECT count(1) FROM svn_commit_path WHERE svn_commit_id = #{svnCommitId} ")
	int selectCountBySvnCommitId(@Param("svnCommitId") int svnCommitId);
	
	@Select("select cp.path, r.id repositoryId, r.name repositoryName, " 
			+ "count(1) commitCount, min(c.revision) minRevision, max(c.revision) maxRevision, " 
			+ "min(c.commit_date) minCommitDate, max(c.commit_date) maxCommitDate " 
			+ "from svn_commit_path cp " 
			+ "INNER join svn_commit c " 
			+ "on cp.svn_commit_id = c.id " 
			+ "INNER JOIN svn_repository r " 
			+ "on c.svn_repository_id = r.id " 
			+ "inner join project_svn_commit pc " 
			+ "on pc.svn_commit_id = cp.svn_commit_id " 
			+ "where pc.project_id = #{projectId} " 
			+ "group by cp.path, r.id, r.name "
			+ "${orderBy} ${limitOffset} ")
	List<PathAndItsSvnRepository> selectByProjectId(
			@Param("projectId") int projectId,
			@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	@Select("select count(1) " 
			+ "from svn_commit_path cp " 
			+ "INNER join svn_commit c " 
			+ "on cp.svn_commit_id = c.id " 
			+ "INNER JOIN svn_repository r " 
			+ "on c.svn_repository_id = r.id " 
			+ "inner join project_svn_commit pc " 
			+ "on pc.svn_commit_id = cp.svn_commit_id " 
			+ "where pc.project_id = #{projectId} " 
			+ "group by cp.path, r.id, r.name ")
	int selectCountByProjectId(@Param("projectId") int projectId);
	
	@Insert("INSERT INTO svn_commit_path (id, svn_commit_id, change_type_id, path, create_user_id) "
			+ "VALUES (#{path.id}, #{path.svnCommitId}, #{path.changeTypeId}, #{path.path}, #{auth.id}) ")
	int insert(@Param("path") SvnCommitPath path, @Param("auth") MVUserDetails auth);
}
