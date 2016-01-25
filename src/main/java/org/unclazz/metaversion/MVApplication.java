package org.unclazz.metaversion;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan
public class MVApplication {
	public static final String APPLICATION_BASE_PACKAGE = MVApplication.class.getPackage().getName();
	public static final String MAPPER_SCAN_BASE_PACKAGE = APPLICATION_BASE_PACKAGE + ".mapper";
	public static final String TYPE_ALIASES_PACKAGE = APPLICATION_BASE_PACKAGE + ".entity";
	public static final String REST_API_PATH_PREFIX = "/api";
	public static final String INIT_PAGE_PATH = "/init";
	public static final String LOGIN_PAGE_PATH = "/login";
	public static final String LOGOUT_PAGE_PATH = "/logout";
	public static final String TOP_PAGE_PATH = "/";

    @Bean
    @Autowired
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
      final SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
      sqlSessionFactoryBean.setDataSource(dataSource);
      sqlSessionFactoryBean.setTypeAliasesPackage(TYPE_ALIASES_PACKAGE);
      return sqlSessionFactoryBean.getObject();
    }
    
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() throws Exception {
    	final MapperScannerConfigurer msc = new MapperScannerConfigurer();
    	msc.setBasePackage(MAPPER_SCAN_BASE_PACKAGE);
    	msc.afterPropertiesSet();
    	return msc;
    }
    
    public static void main(String[] args) throws Exception {
        SpringApplication.run(MVApplication.class, args);
    }
}