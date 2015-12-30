package org.unclazz.metaversion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.DmlType;
import org.unclazz.metaversion.entity.ModifiationType;
import org.unclazz.metaversion.entity.LogImportStatus;
import org.unclazz.metaversion.mapper.DmlTypeMapper;
import org.unclazz.metaversion.mapper.ModifiationTypeMapper;
import org.unclazz.metaversion.mapper.LogImportStatusMapper;

@Service
public class MasterService {
	@Autowired
	private ModifiationTypeMapper modifiationTypeMapper;
	@Autowired
	private DmlTypeMapper dmlTypeMapper;
	@Autowired
	private LogImportStatusMapper svnLogImportStatusMapper;
	
	@Transactional
	public void initializeMaster(final MVUserDetails auth) {
		for (final DmlType v : DmlType.values()) {
			dmlTypeMapper.insert(v, auth);
		}
		
		for (final ModifiationType v : ModifiationType.values()) {
			modifiationTypeMapper.insert(v, auth);
		}
		
		for (final LogImportStatus v : LogImportStatus.values()) {
			svnLogImportStatusMapper.insert(v, auth);
		}
	}
}
