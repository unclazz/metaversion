package org.unclazz.metaversion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unclazz.metaversion.mapper.LogImportMapper;

@Service
public class LogImportService {
	@Autowired
	private LogImportMapper svnLogImportMapper;
	
	
}
