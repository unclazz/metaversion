package org.unclazz.metaversion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MVWebMvcConfiguration extends WebMvcConfigurerAdapter {
	@Autowired
	private MVHandlerInterceptor interceptor;
	
	@Override
	public void addInterceptors(final InterceptorRegistry registry) {
		registry.addInterceptor(interceptor);
	}
}
