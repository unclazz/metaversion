package org.unclazz.metaversion.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.SvnRepository;
import org.unclazz.metaversion.entity.User;
import org.unclazz.metaversion.service.RepositoryService;
import org.unclazz.metaversion.service.UserService;
import org.unclazz.metaversion.vo.Paging;

@RestController
@RequestMapping("/rest")
public class JsonController {
	@Autowired
	private UserService userService;
	@Autowired
	private RepositoryService repositoryService;
	
	@RequestMapping(value="/users", method=RequestMethod.GET)
	public List<User> getUserList(final Principal principal, @ModelAttribute final Paging paging) {
		return userService.getUserList(paging);
	}
	
	@RequestMapping(value="/users/{id}", method=RequestMethod.GET)
	public ResponseEntity<User> getUser(final Principal principal, @PathVariable("id") final int id) {
		final User user = userService.getUser(id);
		if (user == null) {
			return notFound();
		} else {
			return ok(user);
		}
	}
	
	@RequestMapping(value="/users/{id}", method=RequestMethod.PUT)
	public ResponseEntity<User> putUser(final Principal principal,
			@PathVariable("id") final int id,
			@RequestParam("username") final String username, 
			@RequestParam("password") final char[] password, 
			@RequestParam("admin") final boolean admin) {
		
		try {
			final User user = userService.composeValueObject(id, username, password, admin);
			userService.modifyUser(user, MVUserDetails.of(principal));
			return ok(user);
			
		} catch (final RuntimeException e) {
			return internalServerError(e.getMessage());
		}
	}
	
	@RequestMapping(value="/users", method=RequestMethod.POST)
	public ResponseEntity<User> postUser(final Principal principal,
			@RequestParam("username") final String username, 
			@RequestParam("password") final char[] password, 
			@RequestParam("admin") final boolean admin) {
		
		try {
			final User user = userService.composeValueObject(username, password, admin);
			userService.registerUser(user, MVUserDetails.of(principal));
			return ok(user);
			
		} catch (final RuntimeException e) {
			return internalServerError(e.getMessage());
		}
	}
	
	@RequestMapping(value="/users/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<User> deleteUser(final Principal principal, @PathVariable("id") final int id) {
		try {
			userService.removeUser(id, MVUserDetails.of(principal));
			return ok();
			
		} catch (final RuntimeException e) {
			return internalServerError(e.getMessage());
		}
	}
	
	@RequestMapping(value="/repositories", method=RequestMethod.GET)
	public List<SvnRepository> getRepositoryLsit(final Principal principal, @ModelAttribute final Paging paging) {
		return repositoryService.getRepositoryList(paging);
	}
	
	@RequestMapping(value="/repositories/{id}", method=RequestMethod.GET)
	public SvnRepository getRepository(final Principal principal, @PathVariable("id") final int id) {
		return repositoryService.getRepository(id);
	}
	
	public static<T> ResponseEntity<T> ok() {
		return new ResponseEntity<T>(HttpStatus.OK);
	}
	public static<T> ResponseEntity<T> ok(T value) {
		return new ResponseEntity<T>(value, HttpStatus.OK);
	}
	public static<T> ResponseEntity<T> notFound() {
		return new ResponseEntity<T>(HttpStatus.NOT_FOUND);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static<T> ResponseEntity<T> internalServerError(String message) {
		// 戻り値型を揃えるため強引にキャストを行う
		// ＊イレイジャを前提としたトリック
		return (ResponseEntity<T>) new ResponseEntity(message, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
