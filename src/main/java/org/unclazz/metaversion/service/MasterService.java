package org.unclazz.metaversion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unclazz.metaversion.MVUserDetails;
import org.unclazz.metaversion.entity.ChangeType;
import org.unclazz.metaversion.entity.OnlineBatchProgram;
import org.unclazz.metaversion.entity.OnlineBatchStatus;
import org.unclazz.metaversion.mapper.ChangeTypeMapper;
import org.unclazz.metaversion.mapper.OnlineBatchProgramMapper;
import org.unclazz.metaversion.mapper.OnlineBatchStatusMapper;

@Service
public class MasterService {
	@Autowired
	private ChangeTypeMapper chageTypeMapper;
	@Autowired
	private OnlineBatchProgramMapper onlineBatchProgramMapper;
	@Autowired
	private OnlineBatchStatusMapper onlineBatchStatusMapper;
	
	@Transactional
	public void initializeMaster(final MVUserDetails auth) {
		for (final ChangeType value : ChangeType.values()) {
			chageTypeMapper.insert(value);
		}
		for (final OnlineBatchProgram value : OnlineBatchProgram.values()) {
			onlineBatchProgramMapper.insert(value);
		}
		for (final OnlineBatchStatus value : OnlineBatchStatus.values()) {
			onlineBatchStatusMapper.insert(value);
		}
		
	}
}
