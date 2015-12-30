package org.unclazz.metaversion.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.LogImportStatus;

public interface LogImportStatusMapper {
	@Insert("INSERT INTO log_import_status (id, code, create_user_id) "
			+ "VALUES (#{status.id}, #{status.code}, #{auth.id})")
	int insert(@Param("status") LogImportStatus status, @Param("auth") MVUserDetails auth);
}
