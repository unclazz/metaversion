package org.unclazz.metaversion.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.OnlineBatchProgram;
import org.unclazz.metaversion.service.CommitLinkService;
import org.unclazz.metaversion.service.LogImportService;
import org.unclazz.metaversion.vo.BatchResult;

import static org.unclazz.metaversion.MVUtils.*;

@RestController
@RequestMapping("/rest/batches")
public class BatchesJsonController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private LogImportService logImportService;
	@Autowired
	private CommitLinkService commitLinkService;
	
	@RequestMapping(value="/logimport", method=RequestMethod.GET) // TODO
	public ResponseEntity<BatchResult> postLogimport(final Principal principal,
			@RequestParam("repositoryId") final int repositoryId) {
		
		final BatchResult res = BatchResult.ofNowStarting(OnlineBatchProgram.LOG_IMPORT);
		try {
			logImportService.doLogImport(repositoryId, MVUserDetails.of(principal));
			return httpResponseOfOk(res.andEnded());
			
		} catch (final RuntimeException e) {
			logger.error(String.format("Error has occurred at proccess of "
					+ "/rest/batches/logimport (repositoryId=%s).", repositoryId), e);
			return httpResponseOfInternalServerError(res.andAbended(e));
		}
	}
	
	@RequestMapping(value="/commitlink", method=RequestMethod.GET) // TODO
	public ResponseEntity<BatchResult> postCommitlink(final Principal principal,
			@RequestParam("projectId") final int projectId) {
		
		final BatchResult res = BatchResult.ofNowStarting(OnlineBatchProgram.COMMIT_LINK);
		try {
			commitLinkService.doCommitLink(projectId, MVUserDetails.of(principal));
			return httpResponseOfOk(res.andEnded());
			
		} catch (final RuntimeException e) {
			logger.error(String.format("Error has occurred at proccess of "
					+ "/rest/batches/commitlink (projectId=%s).", projectId), e);
			return httpResponseOfInternalServerError(res.andAbended(e));
		}
	}
}
