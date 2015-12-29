package org.unclazz.metaversion.mapper;

import java.util.List;

import org.unclazz.metaversion.entity.SvnLogImportStatus;

public interface CommitLogImportStatusMapper {
	List<SvnLogImportStatus> selectAll();
}
