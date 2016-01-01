package org.unclazz.metaversion.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.MVUtils;
import org.unclazz.metaversion.service.MasterService;
import org.unclazz.metaversion.service.MasterService.ApplicationMayBeAlreadyInitialized;

@Controller
public class HtmlController {
	@Autowired
	private MasterService masterService;
	
	@RequestMapping("/init")
	public String init(final Model model) {
		try {
			masterService.initializeMaster();
			model.addAttribute("successful", true);
		} catch(final ApplicationMayBeAlreadyInitialized ex) {
			model.addAttribute("successful", false);
			model.addAttribute("exceptionName", ex.getClass().getName());
			model.addAttribute("exceptionMessage", ex.getMessage());
			model.addAttribute("exceptionStackTrace", MVUtils.stackTraceToCharSequence(ex));
		}
		return "init";
	}
	
    @RequestMapping("/login")
    public String login() {
        return "login";
    }
    @RequestMapping("/index")
    public String index(Principal principal, Model model) {
    	model.addAttribute("username", MVUserDetails.of(principal).getUsername());
        return "index";
    }
//    @RequestMapping("/foo")
//    public String foo(Principal principal, Model model,
//    		@RequestParam("username") String username,
//    		@RequestParam("password") char[] password) {
//    	final MetaVersionUserDetails ud = MetaVersionUserDetails.of(principal);
////    	if (!ud.isAdmin()) {
////    		throw new RuntimeException("Access denied!!");
////    	}
//    	model.addAttribute("username", ud.getUsername());
//    	userService.registerUser(username, new StringBuilder().append(password), false, ud);
//    	
//        return "index";
//    }
}
