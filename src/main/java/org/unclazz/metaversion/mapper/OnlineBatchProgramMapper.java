package org.unclazz.metaversion.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.unclazz.metaversion.entity.OnlineBatchProgram;

public interface OnlineBatchProgramMapper {
	@Insert("INSERT INTO online_batch_program (id, name) "
			+ "VALUES (#{program.id, typeHandler=org.apache.ibatis.type.ObjectTypeHandler}, "
			+ "#{program.programName, typeHandler=org.apache.ibatis.type.ObjectTypeHandler}) ")
	int insert(@Param("program") OnlineBatchProgram program);
}
