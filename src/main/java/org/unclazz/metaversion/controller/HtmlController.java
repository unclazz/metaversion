package org.unclazz.metaversion.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.MVUtils;
import org.unclazz.metaversion.service.MasterService;
import org.unclazz.metaversion.service.MasterService.ApplicationMayBeAlreadyInitialized;

@Controller
public class HtmlController {
	@Autowired
	private MasterService masterService;
	private Map<String, Object> globalModelAttributes;
	
	@ModelAttribute("global")
	public Map<String, Object> globalModelAttributes() {
		if (globalModelAttributes != null) {
			return globalModelAttributes;
		}
		
		final Map<String, Object> attrs = new HashMap<String, Object>();
		attrs.put("applicationName", "MetaVersion");
		attrs.put("applicationVersion", "1.0-SNAPSHOT");
		
		globalModelAttributes = attrs;
		return globalModelAttributes;
	}
	
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
    
    @RequestMapping({"/", "/index"})
    public String index(Principal principal, Model model) {
    	model.addAttribute("username", MVUserDetails.of(principal).getUsername());
        return "index";
    }
    
    @RequestMapping(value="/apitester", method=RequestMethod.GET)
    public String getApiTester() {
        return "apitester";
    }
    
}
