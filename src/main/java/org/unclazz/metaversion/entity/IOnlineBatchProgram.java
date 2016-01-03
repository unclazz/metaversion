package org.unclazz.metaversion.entity;

import java.util.Map;

public interface IOnlineBatchProgram {
	int getId();
	String getProgramName();
	Map<String, String> getParameters();
}
