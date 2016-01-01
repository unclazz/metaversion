package org.unclazz.metaversion.mapper;

import org.unclazz.metaversion.MVUserDetails;

//TODO MyBatisアノテーションの設定
public interface ProjectSvnRepositoryMapper {
	int insert(int projectId, int svnRepositoryId, MVUserDetails auth);
	int deleteByProjectId(int projectId);
}
