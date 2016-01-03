package org.unclazz.metaversion.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.unclazz.metaversion.entity.IOnlineBatchProgram;

public interface OnlineBatchProgramMapper {
	@Insert("INSERT INTO online_batch_program (id, name) "
			+ "VALUES (#{program.id}, "
			+ "#{program.programName}) ")
	int insert(@Param("program") IOnlineBatchProgram program);
}
