package org.unclazz.metaversion.mapper;

import java.util.List;

import org.unclazz.metaversion.entity.ModifiationType;

public interface ModifiationTypeMapper {
	List<ModifiationType> selectAll();
}
