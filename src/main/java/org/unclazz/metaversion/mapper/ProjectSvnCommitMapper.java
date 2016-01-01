package org.unclazz.metaversion.mapper;

import org.unclazz.metaversion.MVUserDetails;

//TODO MyBatisアノテーションの設定
public interface ProjectSvnCommitMapper {
	int insert(int projectId, int svnCommitId, MVUserDetails auth);
	int deleteByProjectId(int projectId);
}
