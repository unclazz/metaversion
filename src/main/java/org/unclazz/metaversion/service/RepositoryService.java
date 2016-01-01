package org.unclazz.metaversion.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.SvnRepository;
import org.unclazz.metaversion.vo.Paging;

@Service
public class RepositoryService {
	public void registerRepository(SvnRepository repository, MVUserDetails auth) {
		
	}
	public void updateRepository(SvnRepository repository, MVUserDetails auth) {
		
	}
	public List<SvnRepository> getRepositoryList(Paging paging) {
		return null; // TODO
	}
	public SvnRepository getRepository(int id) {
		return null; // TODO
	}
}
