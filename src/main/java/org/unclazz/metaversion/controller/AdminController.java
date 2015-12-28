package org.unclazz.metaversion.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.unclazz.metaversion.MVGrantedAuthority;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.service.UserService;

@Controller
public class AdminController {
	@Autowired
	private UserService userService;
	
    @RequestMapping("/foo")
    public String foo(Principal principal, Model model,
    		@RequestParam("username") String username,
    		@RequestParam("password") char[] password) {
    	final MVUserDetails ud = MVUserDetails.of(principal);
//    	if (!ud.isAdmin()) {
//    		throw new RuntimeException("Access denied!!");
//    	}
    	model.addAttribute("username", ud.getUsername());
    	userService.registerUser(username, new StringBuilder().append(password), false, ud);
    	
        return "index";
    }
}
