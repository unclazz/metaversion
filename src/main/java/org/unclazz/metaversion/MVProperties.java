package org.unclazz.metaversion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class MVProperties {
	@Value("${metaversion.default.admin.id}")
	private int defaultAdminId;
	@Value("${metaversion.default.admin.name}")
	private String defaultAdminName;
	@Value("${metaversion.default.admin.password}")
	private char[] defaultAdminPassword;
	@Value("${metaversion.logimport.revision.range}")
	private int logimportRevisionRange;
	
	public int getDefaultAdminId() {
		return defaultAdminId;
	}
	public String getDefaultAdminName() {
		return defaultAdminName;
	}
	public char[] getDefaultAdminPassword() {
		return defaultAdminPassword;
	}
	public int getLogimportRevisionRange() {
		return logimportRevisionRange;
	}
}
