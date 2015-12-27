package org.unclazz.metaversion.mapper;

import java.util.List;

import org.unclazz.metaversion.entity.CommitLogImportStatus;

public interface CommitLogImportStatusMapper {
	List<CommitLogImportStatus> selectAll();
}
