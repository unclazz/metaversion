package org.unclazz.metaversion.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.OnlineBatchError;

public interface OnlineBatchErrorMapper {
	@Select("SELECT nextval('online_batch_error_seq') ")
	int selectNextVal();
	
	@Insert("INSERT INTO online_batch_error (id, online_batch_log_id, error_name, error_message, create_user_id) "
			+ "VALUES (#{error.id}, #{error.onlineBatchLogId}, #{error.errorName}, #{error.errorMessage}, #{auth.id}) ")
	int insert(@Param("error") OnlineBatchError error, @Param("auth") MVUserDetails auth);
}
