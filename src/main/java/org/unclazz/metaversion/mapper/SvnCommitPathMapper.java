package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.SvnCommitPath;

public interface SvnCommitPathMapper {
	@Select("SELECT nextval('svn_commit_path_seq') ")
	int selectNextVal();
	
	@Select("SELECT id, svn_commit_id svnCommitId, change_type_id changeTypeId, path "
			+ "FROM svn_commit_path WHERE svn_commit_id = #{svnCommitId} ")
	List<SvnCommitPath> selectBySvnCommitId(@Param("svnCommitId") int svnCommitId);
	
	@Insert("INSERT INTO svn_commit_path (id, svn_commit_id, change_type_id, path, create_user_id) "
			+ "VALUES (#{path.id}, #{path.svnCommitId}, #{path.changeTypeId}, #{path.path}, #{auth.id}) ")
	int insert(@Param("path") SvnCommitPath path, @Param("auth") MVUserDetails auth);
}
