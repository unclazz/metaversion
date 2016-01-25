package org.unclazz.metaversion.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.OnlineBatchLock;

public interface OnlineBatchLockMapper {
	@Select("SELECT nextval('online_batch_lock_seq') ")
	int selectNextVal();
	
	@Insert("INSERT INTO online_batch_lock "
			+ "(id, program_id, locked, last_lock_date, last_unlock_date, system_boot_date) "
			+ "VALUES (#{lock.id}, #{lock.programId}, #{lock.locked}, #{lock.lastLockDate}, "
			+ "#{lock.lastUnlockDate}, #{lock.systemBootDate}) ")
	int insert(OnlineBatchLock lock);
	
	@Select("SELECT id, program_id programId, locked, last_lock_date lastLockDate, "
			+ "last_unlock_date lastUnlockDate, system_boot_date systemBootDate "
			+ "FROM online_batch_lock "
			+ "WHERE program_id = #{programId} ")
	OnlineBatchLock selectOneByProgramId(@Param("programId") int programId);
	
	@Select("SELECT id, program_id programId, locked, last_lock_date lastLockDate, "
			+ "last_unlock_date lastUnlockDate, system_boot_date systemBootDate "
			+ "FROM online_batch_lock "
			+ "WHERE program_id = #{programId} "
			+ "AND (locked = false OR system_boot_date < (SELECT max(boot_date) FROM system_boot_log)) "
			+ "FOR UPDATE NOWAIT ")
	OnlineBatchLock selectOneForLockByProgramId(
			@Param("programId") int programId);
	
	@Select("SELECT id, program_id programId, locked, last_lock_date lastLockDate, "
			+ "last_unlock_date lastUnlockDate, system_boot_date systemBootDate "
			+ "FROM online_batch_lock "
			+ "WHERE program_id = #{programId} AND locked = true "
			+ "FOR UPDATE NOWAIT ")
	OnlineBatchLock selectOneForUnLockByProgramId(
			@Param("programId") int programId);
	
	@Update("UPDATE online_batch_lock SET locked = true, last_lock_date = now(),"
			+ "system_boot_date = (SELECT max(boot_date) FROM system_boot_log) "
			+ "WHERE id = #{id} ")
	int updateForLock(@Param("id") int id, @Param("auth") MVUserDetails auth);
	
	@Update("UPDATE online_batch_lock SET locked = false, last_unlock_date = now() WHERE id = #{id} ")
	int updateForUnlock(@Param("id") int id, @Param("auth") MVUserDetails auth);
}
