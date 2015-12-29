package org.unclazz.metaversion.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.DmlType;

public interface DmlTypeMapper {
	@Insert("INSERT INTO dml_type (id, code, name, create_user_id) "
			+ "VALUES (#{type.id}, #{type.code}, #{type.name}, #{auth.id})")
	int insert(@Param("type") DmlType type, @Param("auth") MVUserDetails auth);
}
