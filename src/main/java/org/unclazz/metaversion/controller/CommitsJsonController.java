package org.unclazz.metaversion.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.SvnCommit;
import org.unclazz.metaversion.entity.User;
import org.unclazz.metaversion.service.CommitService;
import org.unclazz.metaversion.service.UserService;
import org.unclazz.metaversion.vo.Paginated;
import org.unclazz.metaversion.vo.Paging;
import static org.unclazz.metaversion.MVUtils.*;

@RestController
@RequestMapping("/rest")
public class CommitsJsonController {
	@Autowired
	private CommitService commitService;
	
	@RequestMapping(value="/commits", method=RequestMethod.GET)
	public Paginated<SvnCommit> getUserList(final Principal principal,
			@ModelAttribute final Paging paging,
			@RequestParam(value="projectId", defaultValue="0") final int projectId) {
		
		return null; // TODO
	}
}
