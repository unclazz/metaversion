package org.unclazz.metaversion.mapper;

import java.util.List;

import org.unclazz.metaversion.entity.LogImportStatus;

public interface CommitLogImportStatusMapper {
	List<LogImportStatus> selectAll();
}
