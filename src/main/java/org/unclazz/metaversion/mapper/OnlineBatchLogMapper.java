package org.unclazz.metaversion.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.OnlineBatchLog;

public interface OnlineBatchLogMapper {
	@Select("SELECT nextval('online_batch_log_seq') ")
	int selectNextVal();
	
	@Select("SELECT id, "
			+ "		program_id programId, "
			+ "		start_date startDate, "
			+ "		end_date endDate, "
			+ "		status_id statusId "
			+ "FROM online_batch_log "
			+ "WHERE id = #{id} ")
	OnlineBatchLog selectOneById(@Param("id") int id);
	
	
	@Select("SELECT id, "
			+ "		program_id programId, "
			+ "		start_date startDate, "
			+ "		end_date endDate, "
			+ "		status_id statusId "
			+ "FROM online_batch_log "
			+ "WHERE id = (SELECT max(id) " 
			+ "		FROM online_batch_log "
			+ "		WHERE program_id = 1) ")
	OnlineBatchLog selectLastOneByProgramId(@Param("programId") int programId);
	
	@Insert("INSERT INTO online_batch_log (id, program_id, start_date, end_date, status_id, "
			+ "create_user_id, update_user_id) "
			+ "VALUES (#{log.id}, #{log.programId}, #{log.startDate}, #{log.endDate}, #{log.statusId}, "
			+ "#{auth.id}, #{auth.id}) ")
	int insert(@Param("log") OnlineBatchLog log, @Param("auth") MVUserDetails auth);

	@Update("UPDATE online_batch_log SET program_id = #{log.programId}, start_date = #{log.startDate}, "
			+ "end_date = #{log.endDate}, status_id = #{log.statusId}, "
			+ "update_date = now(), update_user_id = #{auth.id} "
			+ "WHERE id = #{log.id} ")
	int update(@Param("log") OnlineBatchLog log, @Param("auth") MVUserDetails auth);
}
