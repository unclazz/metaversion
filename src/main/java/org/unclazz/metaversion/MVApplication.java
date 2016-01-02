package org.unclazz.metaversion;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan
public class MVApplication {
	private static final String APPLICATION_BASE_PACKAGE = MVApplication.class.getPackage().getName();
	private static final String MAPPER_SCAN_BASE_PACKAGE = APPLICATION_BASE_PACKAGE + ".mapper";
	private static final String TYPE_ALIASES_PACKAGE = APPLICATION_BASE_PACKAGE + ".entity";

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
    
    @Bean
    public Logger logger() {
    	return LoggerFactory.getLogger(MVApplication.class.getPackage().getName());
    }
    
    public static void main(String[] args) throws Exception {
        SpringApplication.run(MVApplication.class, args);
    }

}