package org.unclazz.metaversion.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.OnlineBatchLock;

public interface OnlineBatchLockMapper {
	@Insert("INSERT INTO online_batch_lock (id, program_id, locked, last_lock_date, last_unlock_date) "
			+ "VALUES (#{lock.id}, #{lock.programId}, #{lock.locked}, #{lock.lastLockDate}, #{lock.lastUnlockDate}) ")
	int insert(OnlineBatchLock lock);
	
	@Select("SELECT id, program_id programId, locked, last_lock_date lastLockDate, last_unlock_date lastUnlockDate "
			+ "FROM online_batch_lock"
			+ "WHERE program_id = #{programId} AND locked = #{locked} "
			+ "FOR UPDATE NOWAIT ")
	OnlineBatchLock selectOneForUpdateNowaitByProgramId(@Param("programId") int programId,
			@Param("locked") boolean locked, @Param("auth") MVUserDetails auth);
	
	@Update("UPDATE online_batch_lock SET locked = true, last_lock_date = now() WHERE id = #{id} ")
	int updateForLock(@Param("id") int id, @Param("auth") MVUserDetails auth);
	
	@Update("UPDATE online_batch_lock SET locked = false, last_unlock_date = now() WHERE id = #{id} ")
	int updateForUnlock(@Param("id") int id, @Param("auth") MVUserDetails auth);
}
