package org.unclazz.metaversion.mapper;

import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.SvnCommitPath;

// TODO MyBatisアノテーションの設定
public interface SvnCommitPathMapper {
	int selectNextVal();
	SvnCommitPath selectOneById(int id);
	int insert(SvnCommitPath commit, MVUserDetails auth);
	int update(SvnCommitPath commit, MVUserDetails auth);
	int delete(int id);
}
