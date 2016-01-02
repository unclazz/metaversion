package org.unclazz.metaversion.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.service.LogImportService;
import static org.unclazz.metaversion.MVUtils.*;

@RestController
@RequestMapping("/rest")
public class BatchesJsonController {
	@Autowired
	private LogImportService logImportService;
	@Autowired
	private Logger logger;
	
	@RequestMapping(value="/batches/logimport", method=RequestMethod.GET) // TODO
	public ResponseEntity<String> putUser(final Principal principal,
			@RequestParam("repositoryId") final int repositoryId) {
		
		try {
			logImportService.doLogImport(repositoryId, MVUserDetails.of(principal));
			return httpResponseOfOk("success");
			
		} catch (final RuntimeException e) {
			logger.error(String.format("Error has occurred at proccess of "
					+ "/rest/batches/logimport (repositoryId=%s).", repositoryId), e);
			return httpResponseOfInternalServerError(e.getMessage());
		}
	}
}
