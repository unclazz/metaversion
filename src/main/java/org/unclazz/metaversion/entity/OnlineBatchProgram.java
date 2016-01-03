package org.unclazz.metaversion.entity;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public enum OnlineBatchProgram implements IOnlineBatchProgram {
	LOG_IMPORTER(1, "logimporter", new String[]{"repositoryId"}),
	P2C_LINKER(2, "p2clinker", new String[]{"projectId"});
	
	private final int id;
	private final String programName;
	private final Map<String, String> parameters;
	
	private OnlineBatchProgram(final int id, final String name, final String[] params) {
		this.id = id;
		this.programName = name;
		final Map<String, String> paramMap = new LinkedHashMap<String, String>(params.length);
		for (int i = 0; i < paramMap.size(); i ++) {
			paramMap.put(String.format("arg%s", i), params[i]);
		}
		this.parameters = Collections.unmodifiableMap(paramMap);
	}
	
	@Override
	public int getId() {
		return id;
	}
	@Override
	public String getProgramName() {
		return programName;
	}
	@Override
	public Map<String, String> getParameters() {
		return parameters;
	}
	
	public static OnlineBatchProgram valueOfProgramName(final String name) {
		for (final OnlineBatchProgram v : values()) {
			if (v.programName.equals(name)) {
				return v;
			}
		}
		throw new IllegalArgumentException();
	}
}
