package org.unclazz.metaversion.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.SvnLogImportStatus;

public interface SvnLogImportStatusMapper {
	@Insert("insert into svn_log_import_status (id, code, create_user_id) "
			+ "values (#{status.id}, #{status.code}, #{auth.id})")
	int insert(@Param("status") SvnLogImportStatus status, @Param("auth") MVUserDetails auth);
}
