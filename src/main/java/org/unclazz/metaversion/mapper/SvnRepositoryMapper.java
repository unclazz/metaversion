package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.SvnRepository;
import org.unclazz.metaversion.entity.SvnRepositoryStats;
import org.unclazz.metaversion.vo.LimitOffsetClause;
import org.unclazz.metaversion.vo.OrderByClause;

public interface SvnRepositoryMapper {
	@Select("SELECT nextval('svn_repository_seq') ")
	int selectNextVal();
	
	@Select("SELECT id, name, base_url baseUrl, "
			+ "trunk_path_pattern trunkPathPattern, branch_path_pattern branchPathPattern, "
			+ "max_revision maxRevision, username, password encodedPassword "
			+ "FROM svn_repository "
			+ "WHERE id = #{id} ")
	SvnRepository selectOneById(int id);

	@Select("SELECT id, name, base_url baseUrl, "
			+ "trunk_path_pattern trunkPathPattern, branch_path_pattern branchPathPattern, "
			+ "max_revision maxRevision, username, password encodedPassword "
			+ "FROM svn_repository "
			+ "${orderBy} ${limitOffset} ")
	List<SvnRepository> selectAll(@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	@Select("SELECT count(1) FROM svn_repository")
	int selectCount();
	
	@Select("SELECT id, name, base_url baseUrl, max_revision maxRevision, "
			+ "commit_count commitCount, path_count pathCount "
			+ "FROM svn_repository_stats_view "
			+ "${orderBy} ${limitOffset} ")
	List<SvnRepositoryStats> selectStatsAll(@Param("orderBy") OrderByClause orderBy,
			@Param("limitOffset") LimitOffsetClause limitOffset);
	
	@Select("SELECT id, name, base_url baseUrl, max_revision maxRevision, "
			+ "commit_count commitCount, path_count pathCount "
			+ "FROM svn_repository_stats_view "
			+ "WHERE id = #{id} ")
	SvnRepositoryStats selectStatsOneById(int id);
	
	@Select("SELECT count(1) FROM svn_repository_stats_view ")
	int selectCountStatsAll();
	
	@Insert("INSERT INTO svn_repository "
			+ "(id, name, base_url, trunk_path_pattern, branch_path_pattern, "
			+ "max_revision, username, password, create_user_id, update_user_id) "
			+ "VALUES (#{repo.id}, #{repo.name}, #{repo.baseUrl}, #{repo.trunkPathPattern}, #{repo.branchPathPattern}, "
			+ "#{repo.maxRevision}, #{repo.username}, #{repo.encodedPassword}, #{auth.id}, #{auth.id}) ")
	int insert(@Param("repo") SvnRepository repo, @Param("auth") MVUserDetails auth);
	
	@Update("UPDATE svn_repository "
			+ "SET name = #{repo.name}, base_url = #{repo.baseUrl}, "
			+ "trunk_path_pattern = #{repo.trunkPathPattern}, branch_path_pattern = #{repo.branchPathPattern}, "
			+ "max_revision = #{repo.maxRevision}, username = #{repo.username}, password = #{repo.encodedPassword},  "
			+ "update_date = now(), update_user_id = #{auth.id} "
			+ "WHERE id = #{repo.id} ")
	int update(@Param("repo") SvnRepository repo, @Param("auth") MVUserDetails auth);
	
	@Delete("DELETE FROM svn_repository WHERE id = #{id} ")
	int delete(@Param("id") int id);
}
