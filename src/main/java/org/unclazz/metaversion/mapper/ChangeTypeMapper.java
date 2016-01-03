package org.unclazz.metaversion.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.unclazz.metaversion.entity.IChangeType;

public interface ChangeTypeMapper {
	@Insert("INSERT INTO change_type (id, code, name) "
			+ "VALUES (#{type.id}, #{type.code}, #{type.typeName}) ")
	int insert(@Param("type") IChangeType type);
}
