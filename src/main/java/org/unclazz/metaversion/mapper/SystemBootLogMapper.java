package org.unclazz.metaversion.mapper;

import java.util.Date;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface SystemBootLogMapper {
	@Select("SELECT max(boot_date) FROM system_boot_log ")
	Date selectMaxBootDate();
	
	@Insert("INSERT INTO system_boot_log (id, boot_date) "
			+ "VALUES (nextval('system_boot_log_seq'), now()) ")
	int insert();
}
