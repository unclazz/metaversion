package org.unclazz.metaversion.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.unclazz.metaversion.service.ProjectParallelsService;

@Controller
public class CsvController {
	@Autowired
	private ProjectParallelsService parallelsService;
	
	@RequestMapping(value = "/csv/projects/{projectId}/parallels",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
 	@ResponseBody
 	public Resource getProjectContentionListCsvDownload(
 			@PathVariable("projectId") final int id) throws IOException {
 		return parallelsService.getProjectParallelsCsvByProjectId(id);
	}
}
