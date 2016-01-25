package org.unclazz.metaversion.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unclazz.metaversion.mapper.SystemBootLogMapper;

@Service
public class SystemBootLogService {
	private final BootDateHolder holder;
	
	@Autowired
	public SystemBootLogService(final SystemBootLogMapper mapper) {
		this.holder = BootDateHolder.getInstance(mapper);
	}
	
	public Date getSystemBootDate() {
		return holder.getValue();
	}
}

class BootDateHolder {
	private static BootDateHolder instance;
	public static synchronized BootDateHolder getInstance(final SystemBootLogMapper mapper) {
		if (instance == null) {
			if (mapper.insert() != 1) {
				throw new RuntimeException("Unexpected error has occurred in system boot process. "
						+ "Insert a record into 'system_boot_log' failed. ");
			}
			instance = new BootDateHolder(mapper.selectMaxBootDate());
		}
		return instance;
	}
	
	private final Date date;
	private BootDateHolder (final Date date) {
		this.date = date;
	}
	public Date getValue() {
		return date;
	}
}