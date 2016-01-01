package org.unclazz.metaversion.mapper;

import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.SvnCommit;

// TODO MyBatisアノテーションの設定
public interface SvnCommitMapper {
	int selectNextVal();
	SvnCommit selectOneById(int id);
	int insert(SvnCommit commit, MVUserDetails auth);
	int update(SvnCommit commit, MVUserDetails auth);
	int delete(int id);
}
