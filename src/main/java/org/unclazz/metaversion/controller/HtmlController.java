package org.unclazz.metaversion.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.unclazz.metaversion.MVProperties;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.MVUtils;
import org.unclazz.metaversion.entity.User;
import org.unclazz.metaversion.service.MasterService;
import org.unclazz.metaversion.service.MasterService.ApplicationMayBeAlreadyInitialized;

@Controller
public class HtmlController {
	@Autowired
	private MasterService masterService;
	@Autowired
	private MVProperties props;
	private Map<String, String> applicationModelAttributes;
	
	@ModelAttribute("user")
	public User userModelAttributes() {
		final MVUserDetails auth = MVUtils.userDetails();
		if (auth != null) {
			return auth.toUser();
		} else {
			return null;
		}
	}
	
	@ModelAttribute("app")
	public Map<String, String> applicationModelAttributes() {
		if (applicationModelAttributes != null) {
			return applicationModelAttributes;
		}
		final Map<String, String> map = new HashMap<String, String>();
		map.put("name", props.getApplicationName());
		map.put("version", props.getApplicationVersion());
		return applicationModelAttributes = map;
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
    public String index(final Principal principal, final Model model) {
        return "index";
    }
    
    @RequestMapping({"/projects"})
    public String projects(final Principal principal, final Model model) {
        return "projects";
    }
    
    @RequestMapping({"/projects/new"})
    public String projectsNew(final Principal principal, final Model model) {
        return "projects$projectId$edit";
    }
    
    @RequestMapping({"/projects/{projectId}/edit"})
    public String projectsProjectIdEdit(final Principal principal, 
    		@PathVariable("projectId") final int projectId, final Model model) {
        return "projects$projectId$edit";
    }
    
    @RequestMapping({"/projects/{projectId}/delete"})
    public String projectsProjectIdDelete(final Principal principal, 
    		@PathVariable("projectId") final int projectId, final Model model) {
        return "projects$projectId$delete";
    }
    
    @RequestMapping({"/projects/{projectId}"})
    public String projectsProjectId(final Principal principal, 
    		@PathVariable("projectId") final int projectId, final Model model) {
        return "projects$projectId";
    }
    
    @RequestMapping({"/projects/{projectId}/commits"})
    public String projectsProjectIdCommits(final Principal principal, 
    		@PathVariable("projectId") final int projectId, final Model model) {
        return "projects$projectId$commits";
    }
    
    @RequestMapping({"/repositories"})
    public String repositories(final Principal principal, final Model model) {
        return "repositories";
    }
    
    @RequestMapping({"/repositories/{repositoryId}"})
    public String repositoriesRepositoryId(final Principal principal, 
    		@PathVariable("repositoryId") final int repositoryId, final Model model) {
        return "index";
    }
    
    @RequestMapping(value="/apitester", method=RequestMethod.GET)
    public String getApiTester() {
        return "apitester";
    }
    
}
