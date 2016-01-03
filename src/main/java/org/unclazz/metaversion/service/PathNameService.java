package org.unclazz.metaversion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unclazz.metaversion.mapper.SvnCommitPathMapper;
import org.unclazz.metaversion.vo.LimitOffsetClause;

@Service
public class PathNameService {
	@Autowired
	private SvnCommitPathMapper svnCommitPathMapper;
	
	public List<String> getPathNameList(final String partialPath, final int size) {
		return svnCommitPathMapper.selectPathByPartialPath(
				partialPath, LimitOffsetClause.ofLimit(size));
	}
}
