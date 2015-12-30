package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.LogImportStackTrace;

public interface LogImportStackTraceMapper {
	@Insert("INSERT INTO log_import_stack_trace (log_import_id, line_number, line, create_user_id) "
			+ "VALUES (#{stackTrace.svnLogImportId}, #{stackTrace.lineNumber}, #{stackTrace.line}, #{auth.id}) ")
	int insert(@Param("stackTrace") LogImportStackTrace stackTrace, @Param("auth") MVUserDetails auth);
	
	@Select("SELECT log_import_id svnLogImportId, line_number lineNumber, line "
			+ "FROM log_import_stack_trace "
			+ "WHERE log_import_id = #{svnLogImportId} "
			+ "ORDER BY line_number ")
	List<LogImportStackTrace> selectBySvnLogImportId(@Param("svnLogImportId") int svnLogImportId);
}
