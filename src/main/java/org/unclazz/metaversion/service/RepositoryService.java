package org.unclazz.metaversion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.MVUtils;
import org.unclazz.metaversion.entity.SvnRepository;
import org.unclazz.metaversion.vo.Paging;

@Service
public class RepositoryService {
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public SvnRepository composeValueObject(final int id, final String name, final String baseUrl,
			final String trunkPathPattern, final String branchPathPattern,
			final int maxRevision, final String username, final char[] password) {
		final SvnRepository r = composeValueObject(name, baseUrl, trunkPathPattern,
				branchPathPattern, maxRevision, username, password);
		r.setId(id);
		return r;
	}
	
	public SvnRepository composeValueObject(final String name, final String baseUrl,
			final String trunkPathPattern, final String branchPathPattern,
			final int maxRevision, final String username, final char[] password) {
		final SvnRepository r = new SvnRepository();
		r.setName(name);
		r.setBaseUrl(baseUrl);
		r.setTrunkPathPattern(trunkPathPattern);
		r.setBranchPathPattern(branchPathPattern);
		r.setMaxRevision(maxRevision);
		r.setUsername(username);
		r.setPassword(passwordEncoder.encode(MVUtils.charArrayToCharSequence(password)));
		return r;
	}
	
	public void registerRepository(SvnRepository repository, MVUserDetails auth) {
		// TODO
	}
	public void modifyRepository(SvnRepository repository, MVUserDetails auth) {
		// TODO
	}
	public void removeRepository(int id, MVUserDetails auth) {
		// TODO
	}
	public List<SvnRepository> getRepositoryList(Paging paging) {
		return null; // TODO
	}
	public SvnRepository getRepository(int id) {
		return null; // TODO
	}
}
