package org.unclazz.metaversion.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.SvnLogImportStackTrace;

public interface SvnLogImportStackTraceMapper {
	@Insert("insert into svn_log_import_stack_trace (svn_log_import_id, line_number, line, create_user_id) "
			+ "values (#{stackTrace.svnLogImportId}, #{stackTrace.lineNumber}, #{stackTrace.line}, #{auth.id}) ")
	int insert(@Param("stackTrace") SvnLogImportStackTrace stackTrace, @Param("auth") MVUserDetails auth);
	
	@Select("select svn_log_import_id svnLogImportId, line_number lineNumber, line "
			+ "from svn_log_import_stack_trace where svn_log_import_id = #{svnLogImportId} "
			+ "order by line_number ")
	List<SvnLogImportStackTrace> selectBySvnLogImportId(@Param("svnLogImportId") int svnLogImportId);
}
