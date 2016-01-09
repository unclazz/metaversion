package org.unclazz.metaversion;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MVWebMvcConfiguration extends WebMvcConfigurerAdapter {
	@Override
	public void addInterceptors(final InterceptorRegistry registry) {
		registry.addInterceptor(new MVHandlerInterceptor());
	}
}
