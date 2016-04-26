package org.unclazz.metaversion.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.unclazz.metaversion.service.CommitService;
import org.unclazz.metaversion.service.ProjectParallelsService;

@Controller
public class CsvController {
	@Autowired
	private ProjectParallelsService parallelsService;
	@Autowired
	private CommitService commitService;
	
	@RequestMapping(value = "/csv/projects/{projectId}/parallels",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE,
			method = RequestMethod.GET)
 	public ResponseEntity<byte[]> getProjectsParallelsListCsvDownload(
 			@PathVariable("projectId") final int id) throws IOException {
 		final byte[] bytes = parallelsService.getProjectParallelsCsvByProjectId(id);
 		final HttpHeaders headers = new HttpHeaders();
 		headers.add("contet-type", MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE + ";Shift_JIS");
 		headers.set("Content-Disposition", String.format("filename=\"parallels_%s.csv\"", id));
 		return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/csv/projects/{projectId}/changedpaths",
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE,
			method = RequestMethod.GET)
 	public ResponseEntity<byte[]> getProjectsChangedPathCsvDownload(
 			@PathVariable("projectId") final int id) throws IOException {
 		final byte[] bytes = commitService.getProjectChangedPathCsvByProjectId(id);
 		final HttpHeaders headers = new HttpHeaders();
 		headers.add("contet-type", MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE + ";Shift_JIS");
 		headers.set("Content-Disposition", String.format("filename=\"changedpaths_%s.csv\"", id));
 		return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
	}
}
