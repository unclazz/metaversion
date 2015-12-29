package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.SvnLogImportStackTrace;

public interface SvnLogImportStackTraceMapper {
	@Insert("INSERT INTO svn_log_import_stack_trace (svn_log_import_id, line_number, line, create_user_id) "
			+ "VALUES (#{stackTrace.svnLogImportId}, #{stackTrace.lineNumber}, #{stackTrace.line}, #{auth.id}) ")
	int insert(@Param("stackTrace") SvnLogImportStackTrace stackTrace, @Param("auth") MVUserDetails auth);
	
	@Select("SELECT svn_log_import_id svnLogImportId, line_number lineNumber, line "
			+ "FROM svn_log_import_stack_trace "
			+ "WHERE svn_log_import_id = #{svnLogImportId} "
			+ "ORDER BY line_number ")
	List<SvnLogImportStackTrace> selectBySvnLogImportId(@Param("svnLogImportId") int svnLogImportId);
}
