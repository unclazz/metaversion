package org.unclazz.metaversion.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.unclazz.metaversion.entity.OnlineBatchStatus;

public interface OnlineBatchStatusMapper {
	@Insert("INSERT INTO online_batch_status (id, name) "
			+ "VALUES (#{status.id, typeHandler=org.apache.ibatis.type.ObjectTypeHandler}, "
			+ "#{status.statusName, typeHandler=org.apache.ibatis.type.ObjectTypeHandler}) ")
	int insert(@Param("status") OnlineBatchStatus status);
}
