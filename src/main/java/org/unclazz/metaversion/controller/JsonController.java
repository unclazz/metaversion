package org.unclazz.metaversion.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.entity.UserHasNoPassword;
import org.unclazz.metaversion.service.UserService;
import org.unclazz.metaversion.vo.Paging;

@RestController
@RequestMapping("/rest")
public class JsonController {
	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/users", method=RequestMethod.GET)
	public List<UserHasNoPassword> getUsers(final Principal principal, @ModelAttribute final Paging paging) {
		return userService.getUserList(paging);
	}
	
	@RequestMapping(value="/users/{id}", method=RequestMethod.GET)
	public UserHasNoPassword getUsers(final Principal principal, @PathVariable("id") final int id) {
		return userService.getUser(id);
	}
}
