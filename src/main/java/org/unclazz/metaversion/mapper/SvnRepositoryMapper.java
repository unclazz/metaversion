package org.unclazz.metaversion.mapper;

import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.SvnRepository;

// TODO MyBatisアノテーションの設定
public interface SvnRepositoryMapper {
	int selectNextVal();
	SvnRepository selectOneById(int id);
	int insert(SvnRepository repo, MVUserDetails auth);
	int update(SvnRepository repo, MVUserDetails auth);
	int delete(int id);
}
