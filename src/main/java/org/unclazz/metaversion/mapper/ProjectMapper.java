package org.unclazz.metaversion.mapper;

import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.Project;

//TODO MyBatisアノテーションの設定
public interface ProjectMapper {
	int selectNextVal();
	Project selectOneById(int id);
	int insert(Project project, MVUserDetails auth);
	int update(Project project, MVUserDetails auth);
	int delete(int id);
}
