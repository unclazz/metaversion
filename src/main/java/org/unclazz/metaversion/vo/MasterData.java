package org.unclazz.metaversion.vo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.unclazz.metaversion.entity.ChangeType;
import org.unclazz.metaversion.entity.OnlineBatchProgram;
import org.unclazz.metaversion.entity.OnlineBatchStatus;

public class MasterData {
	private final static MasterData instance = new MasterData();
	public static MasterData getInstance() {
		return instance;
	}
	
	private final Map<Integer, String> changeTypeMap;
	private final Map<Integer, String> onlineBatchProgramMap;
	private final Map<Integer, String> onlineBatchStatusMap;
	
	private MasterData() {
		final Map<Integer, String> changeTypeMap = new HashMap<Integer, String>();
		final Map<Integer, String> onlineBatchProgramMap = new HashMap<Integer, String>();
		final Map<Integer, String> onlineBatchStatusMap = new HashMap<Integer, String>();
		
		for (final ChangeType v :ChangeType.values()) {
			changeTypeMap.put(v.getId(), v.getTypeName());
		}
		for (final OnlineBatchProgram v : OnlineBatchProgram.values()) {
			onlineBatchProgramMap.put(v.getId(), v.getProgramName());
		}
		for (final OnlineBatchStatus v : OnlineBatchStatus.values()) {
			onlineBatchStatusMap.put(v.getId(), v.getStatusName());
		}
		
		this.changeTypeMap = Collections.unmodifiableMap(changeTypeMap);
		this.onlineBatchProgramMap = Collections.unmodifiableMap(onlineBatchProgramMap);
		this.onlineBatchStatusMap = Collections.unmodifiableMap(onlineBatchStatusMap);
	}
	
	public final Map<Integer, String> getChangeTypeMap() {
		return changeTypeMap;
	}
	public final Map<Integer, String> getOnlineBatchProgramMap() {
		return onlineBatchProgramMap;
	}
	public final Map<Integer, String> getOnlineBatchStatusMap() {
		return onlineBatchStatusMap;
	}
	
}
