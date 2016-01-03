package org.unclazz.metaversion.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.unclazz.metaversion.entity.IOnlineBatchStatus;

public interface OnlineBatchStatusMapper {
	@Insert("INSERT INTO online_batch_status (id, name) "
			+ "VALUES (#{status.id}, "
			+ "#{status.statusName}) ")
	int insert(@Param("status") IOnlineBatchStatus status);
}
